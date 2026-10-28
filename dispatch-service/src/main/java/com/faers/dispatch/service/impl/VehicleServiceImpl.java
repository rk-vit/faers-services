package com.faers.dispatch.service.impl;

import com.faers.dispatch.dao.VehicleDao;
import com.faers.dispatch.dto.RegisterVehicleDto;
import com.faers.dispatch.dto.UpdateVehicleStatusDto;
import com.faers.dispatch.dto.VehicleResponseDto;
import com.faers.dispatch.entity.VehicleEntity;
import com.faers.dispatch.exception.ResourceNotFoundException;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import com.faers.dispatch.service.VehicleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceImpl implements VehicleService {

    private static final Logger log = LoggerFactory.getLogger(VehicleServiceImpl.class);
    private final VehicleDao vehicleDao;

    public VehicleServiceImpl(VehicleDao vehicleDao) {
        this.vehicleDao = vehicleDao;
    }

    @Override
    @Transactional
    public VehicleResponseDto registerVehicle(RegisterVehicleDto dto) {
        if (vehicleDao.existsById(dto.getId())) {
            throw new IllegalArgumentException("Vehicle with ID already exists: " + dto.getId());
        }

        VehicleEntity entity = new VehicleEntity();
        entity.setId(dto.getId());
        entity.setVehicleType(dto.getVehicleType());
        entity.setCapabilityTier(dto.getCapabilityTier());
        entity.setLicensePlate(dto.getLicensePlate());
        entity.setBaseStationId(dto.getBaseStationId());
        entity.setStatus(VehicleStatus.AVAILABLE);
        entity.setCurrentLatitude(dto.getInitialLocation().getLatitude());
        entity.setCurrentLongitude(dto.getInitialLocation().getLongitude());

        VehicleEntity saved = vehicleDao.save(entity);
        log.info("Registered new vehicle: {} ({}) at [{}, {}]",
                saved.getId(), saved.getVehicleType(), saved.getCurrentLatitude(), saved.getCurrentLongitude());
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleResponseDto getVehicleById(String id) {
        VehicleEntity entity = vehicleDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));
        return mapToDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDto> getAllVehicles(VehicleStatus status, VehicleType type) {
        List<VehicleEntity> list;
        if (status != null && type != null) {
            list = vehicleDao.findByStatusAndType(status, type);
        } else if (status != null) {
            list = vehicleDao.findByStatus(status);
        } else if (type != null) {
            list = vehicleDao.findAll().stream()
                    .filter(v -> v.getVehicleType() == type)
                    .collect(Collectors.toList());
        } else {
            list = vehicleDao.findAll();
        }

        return list.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public VehicleResponseDto updateVehicleStatus(String id, UpdateVehicleStatusDto dto) {
        VehicleEntity entity = vehicleDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found with ID: " + id));

        entity.setStatus(dto.getStatus());
        if (dto.getLatitude() != null) {
            entity.setCurrentLatitude(dto.getLatitude());
        }
        if (dto.getLongitude() != null) {
            entity.setCurrentLongitude(dto.getLongitude());
        }

        VehicleEntity updated = vehicleDao.save(entity);
        log.info("Updated vehicle {}: status={}, location=[{}, {}]",
                id, updated.getStatus(), updated.getCurrentLatitude(), updated.getCurrentLongitude());
        return mapToDto(updated);
    }

    private VehicleResponseDto mapToDto(VehicleEntity entity) {
        VehicleResponseDto dto = new VehicleResponseDto();
        dto.setId(entity.getId());
        dto.setVehicleType(entity.getVehicleType());
        dto.setCapabilityTier(entity.getCapabilityTier());
        dto.setLicensePlate(entity.getLicensePlate());
        dto.setStatus(entity.getStatus());
        dto.setCurrentLatitude(entity.getCurrentLatitude());
        dto.setCurrentLongitude(entity.getCurrentLongitude());
        dto.setBaseStationId(entity.getBaseStationId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return dto;
    }
}
