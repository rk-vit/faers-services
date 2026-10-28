package com.faers.dispatch.service.impl;

import com.faers.dispatch.dao.DispatchAssignmentDao;
import com.faers.dispatch.dao.VehicleDao;
import com.faers.dispatch.dto.AllocateVehicleRequestDto;
import com.faers.dispatch.dto.DispatchAssignmentResponseDto;
import com.faers.dispatch.entity.DispatchAssignmentEntity;
import com.faers.dispatch.entity.VehicleEntity;
import com.faers.dispatch.exception.NoVehicleAvailableException;
import com.faers.dispatch.exception.ResourceNotFoundException;
import com.faers.dispatch.model.AssignmentStatus;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import com.faers.dispatch.service.DispatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class DispatchServiceImpl implements DispatchService {

    private static final Logger log = LoggerFactory.getLogger(DispatchServiceImpl.class);

    private final VehicleDao vehicleDao;
    private final DispatchAssignmentDao assignmentDao;
    private final RestClient restClient;

    @Value("${EMERGENCY_SERVICE_URL:http://localhost:8081}")
    private String emergencyServiceUrl;

    public DispatchServiceImpl(VehicleDao vehicleDao,
                               DispatchAssignmentDao assignmentDao) {
        this.vehicleDao = vehicleDao;
        this.assignmentDao = assignmentDao;
        this.restClient = RestClient.create();
    }

    @Override
    @Transactional
    public DispatchAssignmentResponseDto allocateVehicle(AllocateVehicleRequestDto request) {
        log.info("Processing allocation request for emergency: id={}, type={}, severity={}",
                request.getEmergencyRequestId(), request.getEmergencyType(), request.getSeverity());

        double incidentLat = request.getDestination().getLatitude();
        double incidentLon = request.getDestination().getLongitude();

        // 1. Determine vehicle type to search
        VehicleType requiredType = resolveVehicleType(request.getPreferredVehicleType(), request.getEmergencyType());

        // 2. Fetch available candidates (first try matching type, then fallback to any available)
        List<VehicleEntity> candidates = vehicleDao.findByStatusAndType(VehicleStatus.AVAILABLE, requiredType);
        if (candidates.isEmpty()) {
            log.warn("No available vehicles of type {}, falling back to all available vehicles", requiredType);
            candidates = vehicleDao.findByStatus(VehicleStatus.AVAILABLE);
        }

        if (candidates.isEmpty()) {
            log.error("Fleet exhausted! No vehicles available to respond to emergency: {}", request.getEmergencyRequestId());
            throw new NoVehicleAvailableException("No available vehicles in fleet to respond to emergency: " + request.getEmergencyRequestId());
        }

        // 3. Coordinate distance calculation:
        // diffLat = v.lat - incident.lat, diffLon = v.lon - incident.lon
        // Distance score = (diffLat^2 + diffLon^2)
        List<VehicleEntity> sortedCandidates = new ArrayList<>(candidates);
        sortedCandidates.sort(Comparator.comparingDouble(v -> {
            double dLat = v.getCurrentLatitude() - incidentLat;
            double dLon = v.getCurrentLongitude() - incidentLon;
            return (dLat * dLat) + (dLon * dLon);
        }));

        // 4. Concurrency-Safe Locking:
        // Try locking candidate vehicles in order of proximity until one is successfully locked and reserved
        VehicleEntity lockedVehicle = null;
        double chosenDistanceKm = 0.0;
        int chosenEtaSeconds = 0;

        for (VehicleEntity candidate : sortedCandidates) {
            Optional<VehicleEntity> opt = vehicleDao.findByIdForUpdate(candidate.getId());
            if (opt.isPresent()) {
                VehicleEntity v = opt.get();
                if (v.getStatus() == VehicleStatus.AVAILABLE) {
                    // Lock acquired and vehicle is still available!
                    v.setStatus(VehicleStatus.DISPATCHED);
                    lockedVehicle = vehicleDao.save(v);

                    // Compute estimated distance (1 degree ≈ 111 km)
                    double dLat = lockedVehicle.getCurrentLatitude() - incidentLat;
                    double dLon = lockedVehicle.getCurrentLongitude() - incidentLon;
                    double distanceDeg = Math.sqrt((dLat * dLat) + (dLon * dLon));
                    chosenDistanceKm = Math.round(distanceDeg * 111.0 * 100.0) / 100.0;

                    // Estimated ETA assuming 50 km/h emergency speed
                    chosenEtaSeconds = Math.max(60, (int) Math.round((chosenDistanceKm / 50.0) * 3600.0));
                    break;
                }
            }
        }

        if (lockedVehicle == null) {
            log.error("All candidates were locked or taken by concurrent requests for emergency: {}", request.getEmergencyRequestId());
            throw new NoVehicleAvailableException("All matching vehicles were claimed by concurrent dispatches. Retrying queued.");
        }

        log.info("Vehicle {} successfully allocated for emergency {} (distance: {} km, eta: {} s)",
                lockedVehicle.getId(), request.getEmergencyRequestId(), chosenDistanceKm, chosenEtaSeconds);

        // 5. Create assignment record
        String assignmentId = generateAssignmentId();
        DispatchAssignmentEntity assignment = new DispatchAssignmentEntity();
        assignment.setId(assignmentId);
        assignment.setEmergencyRequestId(request.getEmergencyRequestId());
        assignment.setVehicleId(lockedVehicle.getId());
        assignment.setStatus(AssignmentStatus.ASSIGNED);
        assignment.setEstimatedDistanceKm(chosenDistanceKm);
        assignment.setEstimatedEtaSeconds(chosenEtaSeconds);
        assignment.setAssignedAt(Instant.now());

        DispatchAssignmentEntity savedAssignment = assignmentDao.save(assignment);

        // 6. Asynchronously or best-effort notify emergency-request-service
        notifyEmergencyRequestService(request.getEmergencyRequestId(), lockedVehicle.getId(), assignmentId);

        return mapToDto(savedAssignment, lockedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public DispatchAssignmentResponseDto getAssignmentById(String assignmentId) {
        DispatchAssignmentEntity assignment = assignmentDao.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));

        VehicleEntity vehicle = vehicleDao.findById(assignment.getVehicleId())
                .orElse(null);

        return mapToDto(assignment, vehicle);
    }

    @Override
    @Transactional
    public DispatchAssignmentResponseDto completeAssignment(String assignmentId) {
        DispatchAssignmentEntity assignment = assignmentDao.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));

        assignment.setStatus(AssignmentStatus.CLEARED);
        assignment.setCompletedAt(Instant.now());
        DispatchAssignmentEntity updatedAssignment = assignmentDao.save(assignment);

        // Release vehicle back to AVAILABLE
        VehicleEntity vehicle = vehicleDao.findByIdForUpdate(assignment.getVehicleId()).orElse(null);
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleDao.save(vehicle);
            log.info("Assignment {} completed. Vehicle {} released back to AVAILABLE", assignmentId, vehicle.getId());
        }

        return mapToDto(updatedAssignment, vehicle);
    }

    @Override
    @Transactional
    public DispatchAssignmentResponseDto cancelAssignment(String assignmentId, String reason) {
        DispatchAssignmentEntity assignment = assignmentDao.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment not found with ID: " + assignmentId));

        assignment.setStatus(AssignmentStatus.CANCELLED);
        assignment.setFailureReason(reason);
        assignment.setCompletedAt(Instant.now());
        DispatchAssignmentEntity updatedAssignment = assignmentDao.save(assignment);

        // Release vehicle back to AVAILABLE
        VehicleEntity vehicle = vehicleDao.findByIdForUpdate(assignment.getVehicleId()).orElse(null);
        if (vehicle != null) {
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            vehicleDao.save(vehicle);
            log.warn("Assignment {} cancelled (reason: {}). Vehicle {} released to AVAILABLE",
                    assignmentId, reason, vehicle.getId());
        }

        return mapToDto(updatedAssignment, vehicle);
    }

    private VehicleType resolveVehicleType(VehicleType preferred, String emergencyType) {
        if (preferred != null) {
            return preferred;
        }
        if (emergencyType == null) {
            return VehicleType.AMBULANCE;
        }
        return switch (emergencyType.toUpperCase()) {
            case "FIRE" -> VehicleType.FIRE_TENDER;
            case "POLICE" -> VehicleType.POLICE;
            default -> VehicleType.AMBULANCE;
        };
    }

    private void notifyEmergencyRequestService(String emergencyId, String vehicleId, String assignmentId) {
        try {
            String url = emergencyServiceUrl + "/api/v1/emergency-requests/" + emergencyId + "/status";
            Map<String, String> payload = Map.of(
                    "status", "DISPATCHED",
                    "vehicleId", vehicleId,
                    "assignmentId", assignmentId,
                    "remarks", "Dispatched vehicle " + vehicleId
            );

            restClient.patch()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Notified emergency-request-service for {} of assignment {}", emergencyId, assignmentId);
        } catch (Exception e) {
            log.warn("Could not notify emergency-request-service at {} (will sync on polling): {}",
                    emergencyServiceUrl, e.getMessage());
        }
    }

    private String generateAssignmentId() {
        long timestamp = System.currentTimeMillis() % 1000000;
        int random = ThreadLocalRandom.current().nextInt(100, 999);
        return String.format("ASN-%d%d", timestamp, random);
    }

    private DispatchAssignmentResponseDto mapToDto(DispatchAssignmentEntity assignment, VehicleEntity vehicle) {
        DispatchAssignmentResponseDto dto = new DispatchAssignmentResponseDto();
        dto.setAssignmentId(assignment.getId());
        dto.setEmergencyRequestId(assignment.getEmergencyRequestId());
        dto.setVehicleId(assignment.getVehicleId());
        dto.setStatus(assignment.getStatus());
        dto.setEstimatedDistanceKm(assignment.getEstimatedDistanceKm());
        dto.setEstimatedEtaSeconds(assignment.getEstimatedEtaSeconds());
        dto.setAssignedAt(assignment.getAssignedAt());
        dto.setCompletedAt(assignment.getCompletedAt());
        dto.setFailureReason(assignment.getFailureReason());

        if (vehicle != null) {
            dto.setVehicleType(vehicle.getVehicleType());
            dto.setCapabilityTier(vehicle.getCapabilityTier());
        }
        return dto;
    }
}
