package com.faers.dispatch.controller;

import com.faers.dispatch.dto.AllocateVehicleRequestDto;
import com.faers.dispatch.dto.ApiResponse;
import com.faers.dispatch.dto.DispatchAssignmentResponseDto;
import com.faers.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/dispatch")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/allocate")
    public ResponseEntity<ApiResponse<DispatchAssignmentResponseDto>> allocateVehicle(
            @Valid @RequestBody AllocateVehicleRequestDto requestDto) {
        DispatchAssignmentResponseDto response = dispatchService.allocateVehicle(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle allocated and dispatched successfully", response));
    }

    @GetMapping("/assignments/{assignmentId}")
    public ResponseEntity<ApiResponse<DispatchAssignmentResponseDto>> getAssignment(@PathVariable String assignmentId) {
        DispatchAssignmentResponseDto response = dispatchService.getAssignmentById(assignmentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/assignments/{assignmentId}/complete")
    public ResponseEntity<ApiResponse<DispatchAssignmentResponseDto>> completeAssignment(@PathVariable String assignmentId) {
        DispatchAssignmentResponseDto response = dispatchService.completeAssignment(assignmentId);
        return ResponseEntity.ok(ApiResponse.success("Assignment completed. Vehicle released to AVAILABLE.", response));
    }

    @PostMapping("/assignments/{assignmentId}/cancel")
    public ResponseEntity<ApiResponse<DispatchAssignmentResponseDto>> cancelAssignment(
            @PathVariable String assignmentId,
            @RequestParam(required = false, defaultValue = "Cancelled by operator") String reason) {
        DispatchAssignmentResponseDto response = dispatchService.cancelAssignment(assignmentId, reason);
        return ResponseEntity.ok(ApiResponse.success("Assignment cancelled. Vehicle released to AVAILABLE.", response));
    }
}
