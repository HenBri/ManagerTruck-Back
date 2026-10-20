package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentEndRequest;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentsResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.truckdriverassignment.AssignTruckDriverUseCase;
import com.manager.truck.truck.usecase.truckdriverassignment.EndTruckDriverAssignmentUseCase;
import com.manager.truck.truck.usecase.truckdriverassignment.GetAllTruckDriverAssignmentsUseCase;
import com.manager.truck.truck.usecase.truckdriverassignment.GetTruckDriverAssignmentByIdUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TruckDriverAssignmentController.class)
@Import(SecurityConfig.class)
class TruckDriverAssignmentSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AssignTruckDriverUseCase assignTruckDriverUseCase;
    @MockitoBean
    private GetAllTruckDriverAssignmentsUseCase getAllTruckDriverAssignmentsUseCase;
    @MockitoBean
    private GetTruckDriverAssignmentByIdUseCase getTruckDriverAssignmentByIdUseCase;
    @MockitoBean
    private EndTruckDriverAssignmentUseCase endTruckDriverAssignmentUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void listarAsignacionesSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/assignments"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarAsignacionesConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllTruckDriverAssignmentsUseCase.execute(anyLong(), any(), any(), any()))
                .thenReturn(new TruckDriverAssignmentsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/assignments").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarAsignacionesConPartnerDeberiaDevolver200() throws Exception {
        when(getAllTruckDriverAssignmentsUseCase.execute(anyLong(), any(), any(), any()))
                .thenReturn(new TruckDriverAssignmentsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/assignments").with(withRole("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    void listarAsignacionesConDriverDeberiaDevolver200() throws Exception {
        when(getAllTruckDriverAssignmentsUseCase.execute(anyLong(), any(), any(), any()))
                .thenReturn(new TruckDriverAssignmentsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/assignments").with(withRole("DRIVER")))
                .andExpect(status().isOk());
    }

    @Test
    void verAsignacionPorIdConDriverDeberiaDevolver200() throws Exception {
        TruckDriverAssignmentResponse response = new TruckDriverAssignmentResponse();
        response.setAssignmentId(9L);
        when(getTruckDriverAssignmentByIdUseCase.execute(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/companies/1/assignments/9").with(withRole("DRIVER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assignmentId").value(9));
    }

    @Test
    void crearAsignacionConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/assignments").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"truckId\":1,\"driverId\":2,\"assignedFrom\":\"2026-05-01T08:00:00\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearAsignacionConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/assignments").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"truckId\":1,\"driverId\":2,\"assignedFrom\":\"2026-05-01T08:00:00\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearAsignacionConSecretaryDeberiaDevolver201() throws Exception {
        TruckDriverAssignmentResponse response = new TruckDriverAssignmentResponse();
        response.setAssignmentId(9L);
        when(assignTruckDriverUseCase.execute(anyLong(), any(TruckDriverAssignmentRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/companies/1/assignments").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"truckId\":1,\"driverId\":2,\"assignedFrom\":\"2026-05-01T08:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.assignmentId").value(9));
    }

    @Test
    void finalizarAsignacionConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/assignments/9/end").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assignedUntil\":\"2026-05-31T18:00:00\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void finalizarAsignacionConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/assignments/9/end").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assignedUntil\":\"2026-05-31T18:00:00\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void finalizarAsignacionConSecretaryDeberiaDevolver200() throws Exception {
        TruckDriverAssignmentResponse response = new TruckDriverAssignmentResponse();
        response.setAssignmentId(9L);
        when(endTruckDriverAssignmentUseCase.execute(anyLong(), anyLong(),
                any(TruckDriverAssignmentEndRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/companies/1/assignments/9/end").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"assignedUntil\":\"2026-05-31T18:00:00\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void crearAsignacionConSecretarySinFechasDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/assignments").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"truckId\":1,\"driverId\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }
}
