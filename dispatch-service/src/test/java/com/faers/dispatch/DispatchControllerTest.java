package com.faers.dispatch;

import com.faers.dispatch.controller.DispatchController;
import com.faers.dispatch.dto.DispatchAssignmentResponseDto;
import com.faers.dispatch.exception.GlobalExceptionHandler;
import com.faers.dispatch.model.AssignmentStatus;
import com.faers.dispatch.model.CapabilityTier;
import com.faers.dispatch.model.VehicleType;
import com.faers.dispatch.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DispatchControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DispatchService dispatchService;

    @InjectMocks
    private DispatchController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void testAllocateVehicle_Success() throws Exception {
        DispatchAssignmentResponseDto response = new DispatchAssignmentResponseDto();
        response.setAssignmentId("ASN-2001");
        response.setEmergencyRequestId("EMG-1001");
        response.setVehicleId("AMB-042");
        response.setVehicleType(VehicleType.AMBULANCE);
        response.setCapabilityTier(CapabilityTier.ALS);
        response.setStatus(AssignmentStatus.ASSIGNED);
        response.setEstimatedDistanceKm(2.4);
        response.setEstimatedEtaSeconds(180);
        response.setAssignedAt(Instant.now());

        when(dispatchService.allocateVehicle(any())).thenReturn(response);

        String jsonPayload = """
                {
                    "emergencyRequestId": "EMG-1001",
                    "emergencyType": "MEDICAL",
                    "severity": "CRITICAL",
                    "destination": {
                        "latitude": 13.0827,
                        "longitude": 80.2707
                    }
                }
                """;

        mockMvc.perform(post("/api/v1/dispatch/allocate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.assignmentId").value("ASN-2001"))
                .andExpect(jsonPath("$.data.vehicleId").value("AMB-042"))
                .andExpect(jsonPath("$.data.status").value("ASSIGNED"));
    }

    @Test
    void testGetAssignment_Success() throws Exception {
        DispatchAssignmentResponseDto response = new DispatchAssignmentResponseDto();
        response.setAssignmentId("ASN-2001");
        response.setEmergencyRequestId("EMG-1001");
        response.setVehicleId("AMB-042");
        response.setStatus(AssignmentStatus.ASSIGNED);

        when(dispatchService.getAssignmentById("ASN-2001")).thenReturn(response);

        mockMvc.perform(get("/api/v1/dispatch/assignments/ASN-2001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.assignmentId").value("ASN-2001"));
    }
}
