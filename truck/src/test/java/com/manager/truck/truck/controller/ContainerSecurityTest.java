package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.request.ContainerStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.dto.response.ContainersResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.container.GetAllContainersUseCase;
import com.manager.truck.truck.usecase.container.GetContainerByIdUseCase;
import com.manager.truck.truck.usecase.container.SaveContainerUseCase;
import com.manager.truck.truck.usecase.container.UpdateContainerStatusUseCase;
import com.manager.truck.truck.usecase.container.UpdateContainerUseCase;
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

@WebMvcTest(controllers = ContainerController.class)
@Import(SecurityConfig.class)
class ContainerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllContainersUseCase getAllContainersUseCase;
    @MockitoBean
    private GetContainerByIdUseCase getContainerByIdUseCase;
    @MockitoBean
    private SaveContainerUseCase saveContainerUseCase;
    @MockitoBean
    private UpdateContainerUseCase updateContainerUseCase;
    @MockitoBean
    private UpdateContainerStatusUseCase updateContainerStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    private static final String VALID_BODY =
            "{\"chargeId\":1,\"containerCode\":\"CONT-01\",\"containerWeight\":1200.500}";

    @Test
    void listarContenedoresSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/containers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarContenedoresConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllContainersUseCase.execute(anyLong(), any(), any(), any()))
                .thenReturn(new ContainersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/containers").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarContenedoresConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/containers").with(withRole("PARTNER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarContenedoresConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/containers").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearContenedorConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/containers").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearContenedorConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/containers").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearContenedorConSecretaryDeberiaDevolver201() throws Exception {
        ContainerResponse response = new ContainerResponse();
        response.setContainerId(13L);
        when(saveContainerUseCase.execute(anyLong(), any(ContainerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/containers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.containerId").value(13));
    }

    @Test
    void crearContenedorConPesoNegativoDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/containers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chargeId\":1,\"containerCode\":\"CONT-NEG\",\"containerWeight\":-0.5}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void crearContenedorSinCodigoDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/containers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chargeId\":1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearContenedorSinCargaDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/containers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"containerCode\":\"CONT-SIN\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarEstadoContenedorConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/containers/9/status").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"LOADED\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoContenedorConSecretaryDeberiaDevolver200() throws Exception {
        ContainerResponse response = new ContainerResponse();
        response.setContainerId(9L);
        when(updateContainerStatusUseCase.execute(anyLong(), anyLong(), any(ContainerStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/companies/1/containers/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"LOADED\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstadoAValorInvalidoDeberiaDevolver400() throws Exception {
        mockMvc.perform(put("/api/companies/1/containers/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ROTA\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaDeContenedorPorIdConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/containers/9").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }
}
