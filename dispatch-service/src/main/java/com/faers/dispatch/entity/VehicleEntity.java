package com.faers.dispatch.entity;

import com.faers.dispatch.model.CapabilityTier;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "vehicles")
public class VehicleEntity {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", length = 50, nullable = false)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "capability_tier", length = 50, nullable = false)
    private CapabilityTier capabilityTier;

    @Column(name = "license_plate", length = 50, nullable = false, unique = true)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50, nullable = false)
    private VehicleStatus status = VehicleStatus.AVAILABLE;

    @Column(name = "current_latitude", nullable = false)
    private Double currentLatitude;

    @Column(name = "current_longitude", nullable = false)
    private Double currentLongitude;

    @Column(name = "base_station_id", length = 64)
    private String baseStationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public VehicleEntity() {
    }

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
        if (status == null) {
            status = VehicleStatus.AVAILABLE;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getBaseStationId() {
        return baseStationId;
    }

    public void setBaseStationId(String baseStationId) {
        this.baseStationId = baseStationId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
