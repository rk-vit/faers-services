package com.faers.emergency.event;

import com.faers.emergency.dao.EmergencyRequestDao;
import com.faers.emergency.entity.EmergencyRequestEntity;
import com.faers.emergency.model.EmergencyStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class EmergencyDispatchTrigger {

    private static final Logger log = LoggerFactory.getLogger(EmergencyDispatchTrigger.class);

    private final EmergencyRequestDao emergencyRequestDao;
    private final RestClient restClient;

    @Value("${dispatch.service.url:http://localhost:8082}")
    private String dispatchServiceUrl;

    public EmergencyDispatchTrigger(EmergencyRequestDao emergencyRequestDao) {
        this.emergencyRequestDao = emergencyRequestDao;
        this.restClient = RestClient.create();
    }

    @EventListener
    public void onEmergencyAccepted(EmergencyRequestAcceptedEvent event) {
        if (event.getLocation() == null || event.getLocation().getLatitude() == null || event.getLocation().getLongitude() == null) {
            log.warn("Emergency {} has no coordinates, skipping auto-dispatch trigger", event.getEmergencyRequestId());
            return;
        }

        log.info("Triggering auto-dispatch for emergency {} at URL: {}", event.getEmergencyRequestId(), dispatchServiceUrl);

        try {
            Map<String, Object> payload = Map.of(
                    "emergencyRequestId", event.getEmergencyRequestId(),
                    "emergencyType", event.getEmergencyType().name(),
                    "severity", event.getSeverity().name(),
                    "destination", Map.of(
                            "latitude", event.getLocation().getLatitude(),
                            "longitude", event.getLocation().getLongitude()
                    )
            );

            Map response = restClient.post()
                    .uri(dispatchServiceUrl + "/api/v1/dispatch/allocate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .body(Map.class);

            if (response != null && Boolean.TRUE.equals(response.get("success"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data != null) {
                    String vehicleId = (String) data.get("vehicleId");
                    String assignmentId = (String) data.get("assignmentId");

                    emergencyRequestDao.findById(event.getEmergencyRequestId()).ifPresent(entity -> {
                        entity.setStatus(EmergencyStatus.DISPATCHED);
                        entity.setAssignedVehicleId(vehicleId);
                        entity.setAssignedAssignmentId(assignmentId);
                        emergencyRequestDao.save(entity);
                        log.info("Emergency {} updated to DISPATCHED with vehicle {} and assignment {}",
                                event.getEmergencyRequestId(), vehicleId, assignmentId);
                    });
                }
            }
        } catch (Exception e) {
            log.warn("Dispatch service call to {} for emergency {} failed or service unavailable: {}. Request remains queued as DISPATCH_PENDING.",
                    dispatchServiceUrl, event.getEmergencyRequestId(), e.getMessage());
        }
    }
}
