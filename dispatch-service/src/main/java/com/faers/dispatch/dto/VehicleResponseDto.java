package com.faers.dispatch.dto;

import com.faers.dispatch.model.CapabilityTier;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import java.time.Instant;

public class VehicleResponseDto {

    private String id;
    private VehicleType vehicleType;
    private CapabilityTier capabilityTier;
    private String licensePlate;
    private VehicleStatus status;
    private Double currentLatitude;
    private Double currentLongitude;
    private String baseStationId;
    private Instant createdAt;
    private Instant updatedAt;

    public VehicleResponseDto() {
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
