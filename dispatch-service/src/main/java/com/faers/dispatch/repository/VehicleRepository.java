package com.faers.dispatch.repository;

import com.faers.dispatch.entity.VehicleEntity;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, String> {

    List<VehicleEntity> findByStatus(VehicleStatus status);

    List<VehicleEntity> findByStatusAndVehicleType(VehicleStatus status, VehicleType vehicleType);

    List<VehicleEntity> findByVehicleType(VehicleType vehicleType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM VehicleEntity v WHERE v.id = :id")
    Optional<VehicleEntity> findByIdForUpdate(@Param("id") String id);
}
