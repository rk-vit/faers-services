package com.faers.dispatch.dto;

import com.faers.dispatch.model.CapabilityTier;
import com.faers.dispatch.model.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RegisterVehicleDto {

    @NotBlank(message = "Vehicle ID is required")
    private String id;

    @NotNull(message = "Vehicle type is required")
    private VehicleType vehicleType;

    @NotNull(message = "Capability tier is required")
    private CapabilityTier capabilityTier;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    private String baseStationId;

    @NotNull(message = "Initial location is required")
    @Valid
    private LocationDto initialLocation;

    public RegisterVehicleDto() {
    }

    public RegisterVehicleDto(String id, VehicleType vehicleType, CapabilityTier capabilityTier,
                              String licensePlate, String baseStationId, LocationDto initialLocation) {
        this.id = id;
        this.vehicleType = vehicleType;
        this.capabilityTier = capabilityTier;
        this.licensePlate = licensePlate;
        this.baseStationId = baseStationId;
        this.initialLocation = initialLocation;
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

    public String getBaseStationId() {
        return baseStationId;
    }

    public void setBaseStationId(String baseStationId) {
        this.baseStationId = baseStationId;
    }

    public LocationDto getInitialLocation() {
        return initialLocation;
    }

    public void setInitialLocation(LocationDto initialLocation) {
        this.initialLocation = initialLocation;
    }
}
