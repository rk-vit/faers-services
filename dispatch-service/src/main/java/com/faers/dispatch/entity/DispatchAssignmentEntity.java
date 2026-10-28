package com.faers.dispatch.entity;

import com.faers.dispatch.model.AssignmentStatus;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "dispatch_assignments")
public class DispatchAssignmentEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "emergency_request_id", length = 64, nullable = false)
    private String emergencyRequestId;

    @Column(name = "vehicle_id", length = 64, nullable = false)
    private String vehicleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private AssignmentStatus status = AssignmentStatus.ASSIGNED;

    @Column(name = "estimated_distance_km")
    private Double estimatedDistanceKm;

    @Column(name = "estimated_eta_seconds")
    private Integer estimatedEtaSeconds;

    @Column(name = "assigned_at", nullable = false, updatable = false)
    private Instant assignedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;

    public DispatchAssignmentEntity() {
    }

    @PrePersist
    protected void onCreate() {
        if (assignedAt == null) {
            assignedAt = Instant.now();
        }
        if (status == null) {
            status = AssignmentStatus.ASSIGNED;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
