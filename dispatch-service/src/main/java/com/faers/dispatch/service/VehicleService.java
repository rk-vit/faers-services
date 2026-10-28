package com.faers.dispatch.service;

import com.faers.dispatch.dto.RegisterVehicleDto;
import com.faers.dispatch.dto.UpdateVehicleStatusDto;
import com.faers.dispatch.dto.VehicleResponseDto;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;

import java.util.List;

public interface VehicleService {

    VehicleResponseDto registerVehicle(RegisterVehicleDto dto);

    VehicleResponseDto getVehicleById(String id);

    List<VehicleResponseDto> getAllVehicles(VehicleStatus status, VehicleType type);

    VehicleResponseDto updateVehicleStatus(String id, UpdateVehicleStatusDto dto);
}
