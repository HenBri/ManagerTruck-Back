package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.journey.GetAllJourneysUseCase;
import com.manager.truck.truck.usecase.journey.GetJourneyByIdUseCase;
import com.manager.truck.truck.usecase.journey.SaveJourneyUseCase;
import com.manager.truck.truck.usecase.journey.UpdateJourneyStatusUseCase;
import com.manager.truck.truck.usecase.journey.UpdateJourneyUseCase;
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

@WebMvcTest(controllers = JourneyController.class)
@Import(SecurityConfig.class)
class JourneySecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllJourneysUseCase getAllJourneysUseCase;
    @MockitoBean
    private GetJourneyByIdUseCase getJourneyByIdUseCase;
    @MockitoBean
    private SaveJourneyUseCase saveJourneyUseCase;
    @MockitoBean
    private UpdateJourneyUseCase updateJourneyUseCase;
    @MockitoBean
    private UpdateJourneyStatusUseCase updateJourneyStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void listarViajesSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/journeys"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarViajesConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllJourneysUseCase.execute(anyLong(), any(), any(), any(), any()))
                .thenReturn(new JourneysResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/journeys").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarViajesConPartnerDeberiaDevolver200() throws Exception {
        when(getAllJourneysUseCase.execute(anyLong(), any(), any(), any(), any()))
                .thenReturn(new JourneysResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/journeys").with(withRole("PARTNER")))
                .andExpect(status().isOk());
    }

    @Test
    void listarViajesConDriverDeberiaDevolver200() throws Exception {
        when(getAllJourneysUseCase.execute(anyLong(), any(), any(), any(), any()))
                .thenReturn(new JourneysResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/journeys").with(withRole("DRIVER")))
                .andExpect(status().isOk());
    }

    @Test
    void listarViajesConSuperAdminDeberiaDevolver200() throws Exception {
        when(getAllJourneysUseCase.execute(anyLong(), any(), any(), any(), any()))
                .thenReturn(new JourneysResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/journeys").with(withRole("SUPER_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void crearViajeConSecretaryDeberiaDevolver201() throws Exception {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(9L);
        when(saveJourneyUseCase.execute(anyLong(), any(JourneyRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/journeys").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"containerId\":1,\"journeyOrigin\":\"A\",\"journeyDestination\":\"B\","
                                + "\"executionType\":\"PARTNER\",\"assignmentId\":1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.journeyId").value(9));
    }

    @Test
    void crearViajeConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/journeys").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"containerId\":1,\"journeyOrigin\":\"A\",\"journeyDestination\":\"B\","
                                + "\"executionType\":\"PARTNER\",\"assignmentId\":1}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearViajeConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/journeys").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"containerId\":1,\"journeyOrigin\":\"A\",\"journeyDestination\":\"B\","
                                + "\"executionType\":\"PARTNER\",\"assignmentId\":1}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizarViajeConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/journeys/9").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"journeyOrigin\":\"A\",\"journeyDestination\":\"B\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void actualizarViajeConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/journeys/9").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"journeyOrigin\":\"A\",\"journeyDestination\":\"B\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizarEstadoConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/journeys/9/status").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void actualizarEstadoConSecretaryDeberiaDevolver200() throws Exception {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(9L);
        when(updateJourneyStatusUseCase.execute(anyLong(), anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/api/companies/1/journeys/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.journeyId").value(9));
    }

    @Test
    void actualizarEstadoConDriverDeberiaDevolver200() throws Exception {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(9L);
        when(updateJourneyStatusUseCase.execute(anyLong(), anyLong(), any())).thenReturn(response);

        mockMvc.perform(put("/api/companies/1/journeys/9/status").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_PROGRESS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.journeyId").value(9));
    }

    @Test
    void verViajePorIdConPartnerDeberiaDevolver200() throws Exception {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(9L);
        when(getJourneyByIdUseCase.execute(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/companies/1/journeys/9").with(withRole("PARTNER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.journeyId").value(9));
    }

    @Test
    void verViajePorIdConDriverDeberiaDevolver200() throws Exception {
        JourneyResponse response = new JourneyResponse();
        response.setJourneyId(9L);
        when(getJourneyByIdUseCase.execute(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/companies/1/journeys/9").with(withRole("DRIVER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.journeyId").value(9));
    }
}
