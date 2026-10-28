package com.faers.dispatch.dto;

import com.faers.dispatch.model.AssignmentStatus;
import com.faers.dispatch.model.CapabilityTier;
import com.faers.dispatch.model.VehicleType;
import java.time.Instant;

public class DispatchAssignmentResponseDto {

    private String assignmentId;
    private String emergencyRequestId;
    private String vehicleId;
    private VehicleType vehicleType;
    private CapabilityTier capabilityTier;
    private AssignmentStatus status;
    private Double estimatedDistanceKm;
    private Integer estimatedEtaSeconds;
    private Instant assignedAt;
    private Instant completedAt;
    private String failureReason;

    public DispatchAssignmentResponseDto() {
    }

    public String getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(String assignmentId) {
        this.assignmentId = assignmentId;
    }

    public String getEmergencyRequestId() {
        return emergencyRequestId;
    }

    public void setEmergencyRequestId(String emergencyRequestId) {
        this.emergencyRequestId = emergencyRequestId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public CapabilityTier getCapabilityTier() {
        return capabilityTier;
    }

    public void setCapabilityTier(CapabilityTier capabilityTier) {
        this.capabilityTier = capabilityTier;
    }

    public AssignmentStatus getStatus() {
        return status;
    }

    public void setStatus(AssignmentStatus status) {
        this.status = status;
    }

    public Double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public void setEstimatedDistanceKm(Double estimatedDistanceKm) {
        this.estimatedDistanceKm = estimatedDistanceKm;
    }

    public Integer getEstimatedEtaSeconds() {
        return estimatedEtaSeconds;
    }

    public void setEstimatedEtaSeconds(Integer estimatedEtaSeconds) {
        this.estimatedEtaSeconds = estimatedEtaSeconds;
    }

    public Instant getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(Instant assignedAt) {
        this.assignedAt = assignedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}
