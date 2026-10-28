package com.faers.dispatch.dto;

import com.faers.dispatch.model.VehicleStatus;
import jakarta.validation.constraints.NotNull;

public class UpdateVehicleStatusDto {

    @NotNull(message = "Status is required")
    private VehicleStatus status;

    private Double latitude;
    private Double longitude;

    public UpdateVehicleStatusDto() {
    }

    public UpdateVehicleStatusDto(VehicleStatus status, Double latitude, Double longitude) {
        this.status = status;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
