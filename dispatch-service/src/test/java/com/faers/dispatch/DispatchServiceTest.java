package com.faers.dispatch;

import com.faers.dispatch.dao.DispatchAssignmentDao;
import com.faers.dispatch.dao.VehicleDao;
import com.faers.dispatch.dto.AllocateVehicleRequestDto;
import com.faers.dispatch.dto.DispatchAssignmentResponseDto;
import com.faers.dispatch.dto.LocationDto;
import com.faers.dispatch.entity.DispatchAssignmentEntity;
import com.faers.dispatch.entity.VehicleEntity;
import com.faers.dispatch.exception.NoVehicleAvailableException;
import com.faers.dispatch.model.AssignmentStatus;
import com.faers.dispatch.model.CapabilityTier;
import com.faers.dispatch.model.VehicleStatus;
import com.faers.dispatch.model.VehicleType;
import com.faers.dispatch.service.impl.DispatchServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private VehicleDao vehicleDao;

    @Mock
    private DispatchAssignmentDao assignmentDao;

    @InjectMocks
    private DispatchServiceImpl dispatchService;

    private VehicleEntity vFar;
    private VehicleEntity vNear;

    @BeforeEach
    void setUp() {
        // Incident at [13.0800, 80.2700]
        vFar = new VehicleEntity();
        vFar.setId("AMB-FAR");
        vFar.setVehicleType(VehicleType.AMBULANCE);
        vFar.setCapabilityTier(CapabilityTier.BLS);
        vFar.setStatus(VehicleStatus.AVAILABLE);
        vFar.setCurrentLatitude(13.1500); // further away
        vFar.setCurrentLongitude(80.2900);

        vNear = new VehicleEntity();
        vNear.setId("AMB-NEAR");
        vNear.setVehicleType(VehicleType.AMBULANCE);
        vNear.setCapabilityTier(CapabilityTier.ALS);
        vNear.setStatus(VehicleStatus.AVAILABLE);
        vNear.setCurrentLatitude(13.0820); // very close!
        vNear.setCurrentLongitude(80.2710);
    }

    @Test
    void testAllocateVehicle_PicksNearestVehicle() {
        // Provide both vehicles in arbitrary order
        when(vehicleDao.findByStatusAndType(VehicleStatus.AVAILABLE, VehicleType.AMBULANCE))
                .thenReturn(List.of(vFar, vNear));

        when(vehicleDao.findByIdForUpdate("AMB-NEAR")).thenReturn(Optional.of(vNear));
        when(vehicleDao.save(any(VehicleEntity.class))).thenAnswer(i -> i.getArgument(0));

        when(assignmentDao.save(any(DispatchAssignmentEntity.class))).thenAnswer(i -> {
            DispatchAssignmentEntity e = i.getArgument(0);
            return e;
        });

        AllocateVehicleRequestDto req = new AllocateVehicleRequestDto(
                "EMG-1001",
                "MEDICAL",
                "CRITICAL",
                VehicleType.AMBULANCE,
                new LocationDto(13.0800, 80.2700)
        );

        DispatchAssignmentResponseDto response = dispatchService.allocateVehicle(req);

        assertNotNull(response);
        // Assert that the NEAREST vehicle was selected!
        assertEquals("AMB-NEAR", response.getVehicleId());
        assertEquals(AssignmentStatus.ASSIGNED, response.getStatus());
        assertEquals(VehicleStatus.DISPATCHED, vNear.getStatus());

        verify(vehicleDao, times(1)).findByIdForUpdate("AMB-NEAR");
    }

    @Test
    void testAllocateVehicle_FleetExhaustedThrowsException() {
        when(vehicleDao.findByStatusAndType(VehicleStatus.AVAILABLE, VehicleType.AMBULANCE))
                .thenReturn(Collections.emptyList());
        when(vehicleDao.findByStatus(VehicleStatus.AVAILABLE))
                .thenReturn(Collections.emptyList());

        AllocateVehicleRequestDto req = new AllocateVehicleRequestDto(
                "EMG-1001",
                "MEDICAL",
                "CRITICAL",
                VehicleType.AMBULANCE,
                new LocationDto(13.0800, 80.2700)
        );

        assertThrows(NoVehicleAvailableException.class, () -> dispatchService.allocateVehicle(req));
    }

    @Test
    void testCompleteAssignment_ReleasesVehicleToAvailable() {
        DispatchAssignmentEntity assignment = new DispatchAssignmentEntity();
        assignment.setId("ASN-2001");
        assignment.setVehicleId("AMB-NEAR");
        assignment.setStatus(AssignmentStatus.ASSIGNED);

        vNear.setStatus(VehicleStatus.DISPATCHED);

        when(assignmentDao.findById("ASN-2001")).thenReturn(Optional.of(assignment));
        when(assignmentDao.save(any(DispatchAssignmentEntity.class))).thenAnswer(i -> i.getArgument(0));
        when(vehicleDao.findByIdForUpdate("AMB-NEAR")).thenReturn(Optional.of(vNear));
        when(vehicleDao.save(any(VehicleEntity.class))).thenAnswer(i -> i.getArgument(0));

        DispatchAssignmentResponseDto response = dispatchService.completeAssignment("ASN-2001");

        assertEquals(AssignmentStatus.CLEARED, response.getStatus());
        assertEquals(VehicleStatus.AVAILABLE, vNear.getStatus());
        verify(vehicleDao, times(1)).save(vNear);
    }
}
