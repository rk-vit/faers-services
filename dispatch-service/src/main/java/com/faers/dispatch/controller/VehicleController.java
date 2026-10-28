package com.faers.dispatch.controller;

import com.faers.dispatch.dto.ApiResponse;
import com.faers.dispatch.dto.RegisterVehicleDto;
import com.faers.dispatch.dto.UpdateVehicleStatusDto;
import com.faers.dispatch.dto.VehicleResponseDto;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import com.faers.dispatch.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VehicleResponseDto>> registerVehicle(@Valid @RequestBody RegisterVehicleDto requestDto) {
        VehicleResponseDto response = vehicleService.registerVehicle(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle registered successfully", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VehicleResponseDto>>> getAllVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) VehicleType vehicleType) {
        List<VehicleResponseDto> list = vehicleService.getAllVehicles(status, vehicleType);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> getVehicleById(@PathVariable String id) {
        VehicleResponseDto response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<VehicleResponseDto>> updateVehicleStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateVehicleStatusDto updateDto) {
        VehicleResponseDto response = vehicleService.updateVehicleStatus(id, updateDto);
        return ResponseEntity.ok(ApiResponse.success("Vehicle status updated", response));
    }
}
