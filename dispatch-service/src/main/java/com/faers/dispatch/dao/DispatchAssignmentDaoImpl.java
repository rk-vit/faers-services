package com.faers.dispatch.dao;

import com.faers.dispatch.entity.DispatchAssignmentEntity;
import com.faers.dispatch.model.AssignmentStatus;
import com.faers.dispatch.repository.DispatchAssignmentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class DispatchAssignmentDaoImpl implements DispatchAssignmentDao {

    private final DispatchAssignmentRepository repository;

    public DispatchAssignmentDaoImpl(DispatchAssignmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public DispatchAssignmentEntity save(DispatchAssignmentEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<DispatchAssignmentEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<DispatchAssignmentEntity> findByEmergencyRequestId(String emergencyRequestId) {
        return repository.findByEmergencyRequestId(emergencyRequestId);
    }

    @Override
    public List<DispatchAssignmentEntity> findByVehicleId(String vehicleId) {
        return repository.findByVehicleId(vehicleId);
    }

    @Override
    public List<DispatchAssignmentEntity> findByStatus(AssignmentStatus status) {
        return repository.findByStatus(status);
    }
}
