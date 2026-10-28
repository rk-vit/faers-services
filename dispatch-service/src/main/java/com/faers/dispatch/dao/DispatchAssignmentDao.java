package com.faers.dispatch.dao;

import com.faers.dispatch.entity.DispatchAssignmentEntity;
import com.faers.dispatch.model.AssignmentStatus;

import java.util.List;
import java.util.Optional;

public interface DispatchAssignmentDao {

    DispatchAssignmentEntity save(DispatchAssignmentEntity entity);

    Optional<DispatchAssignmentEntity> findById(String id);

    List<DispatchAssignmentEntity> findByEmergencyRequestId(String emergencyRequestId);

    List<DispatchAssignmentEntity> findByVehicleId(String vehicleId);

    List<DispatchAssignmentEntity> findByStatus(AssignmentStatus status);
}
