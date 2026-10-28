package com.faers.dispatch.dao;

import com.faers.dispatch.entity.VehicleEntity;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import com.faers.dispatch.repository.VehicleRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class VehicleDaoImpl implements VehicleDao {

    private final VehicleRepository repository;

    public VehicleDaoImpl(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public VehicleEntity save(VehicleEntity entity) {
        return repository.save(entity);
    }

    @Override
    public Optional<VehicleEntity> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<VehicleEntity> findByIdForUpdate(String id) {
        return repository.findByIdForUpdate(id);
    }

    @Override
    public List<VehicleEntity> findAll() {
        return repository.findAll();
    }

    @Override
    public List<VehicleEntity> findByStatus(VehicleStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public List<VehicleEntity> findByStatusAndType(VehicleStatus status, VehicleType type) {
        return repository.findByStatusAndVehicleType(status, type);
    }

    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }
}
