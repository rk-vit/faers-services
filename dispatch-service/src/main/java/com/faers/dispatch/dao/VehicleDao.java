package com.faers.dispatch.dao;

import com.faers.dispatch.entity.VehicleEntity;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;

import java.util.List;
import java.util.Optional;

public interface VehicleDao {

    VehicleEntity save(VehicleEntity entity);

    Optional<VehicleEntity> findById(String id);

    Optional<VehicleEntity> findByIdForUpdate(String id);

    List<VehicleEntity> findAll();

    List<VehicleEntity> findByStatus(VehicleStatus status);

    List<VehicleEntity> findByStatusAndType(VehicleStatus status, VehicleType type);

    boolean existsById(String id);
}
