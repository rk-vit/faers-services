package com.faers.dispatch.dto;

import com.faers.dispatch.model.VehicleType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AllocateVehicleRequestDto {

    @NotBlank(message = "Emergency request ID is required")
    private String emergencyRequestId;

    private String emergencyType; // e.g. MEDICAL, FIRE, ACCIDENT, POLICE

    private String severity; // e.g. CRITICAL, HIGH, MEDIUM, LOW

    private VehicleType preferredVehicleType; // optional preference

    @NotNull(message = "Destination location is required")
    @Valid
    private LocationDto destination;

    public AllocateVehicleRequestDto() {
    }

    public AllocateVehicleRequestDto(String emergencyRequestId, String emergencyType, String severity,
                                    VehicleType preferredVehicleType, LocationDto destination) {
        this.emergencyRequestId = emergencyRequestId;
        this.emergencyType = emergencyType;
        this.severity = severity;
        this.preferredVehicleType = preferredVehicleType;
        this.destination = destination;
    }

    public String getEmergencyRequestId() {
        return emergencyRequestId;
    }

    public void setEmergencyRequestId(String emergencyRequestId) {
        this.emergencyRequestId = emergencyRequestId;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public VehicleType getPreferredVehicleType() {
        return preferredVehicleType;
    }

    public void setPreferredVehicleType(VehicleType preferredVehicleType) {
        this.preferredVehicleType = preferredVehicleType;
    }

    public LocationDto getDestination() {
        return destination;
    }

    public void setDestination(LocationDto destination) {
        this.destination = destination;
    }
}
