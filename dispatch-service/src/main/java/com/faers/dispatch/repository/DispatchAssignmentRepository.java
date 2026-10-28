package com.faers.dispatch.repository;

import com.faers.dispatch.entity.DispatchAssignmentEntity;
import com.faers.dispatch.model.AssignmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchAssignmentRepository extends JpaRepository<DispatchAssignmentEntity, String> {

    List<DispatchAssignmentEntity> findByEmergencyRequestId(String emergencyRequestId);

    List<DispatchAssignmentEntity> findByVehicleId(String vehicleId);

    List<DispatchAssignmentEntity> findByStatus(AssignmentStatus status);

    Optional<DispatchAssignmentEntity> findFirstByEmergencyRequestIdOrderByAssignedAtDesc(String emergencyRequestId);
}
