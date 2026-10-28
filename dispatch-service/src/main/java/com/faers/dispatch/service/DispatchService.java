package com.faers.dispatch.service;

import com.faers.dispatch.dto.AllocateVehicleRequestDto;
import com.faers.dispatch.dto.DispatchAssignmentResponseDto;

public interface DispatchService {

    DispatchAssignmentResponseDto allocateVehicle(AllocateVehicleRequestDto request);

    DispatchAssignmentResponseDto getAssignmentById(String assignmentId);

    DispatchAssignmentResponseDto completeAssignment(String assignmentId);

    DispatchAssignmentResponseDto cancelAssignment(String assignmentId, String reason);
}
