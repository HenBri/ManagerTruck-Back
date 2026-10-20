package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.request.ClientStatusRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.dto.response.ClientsResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.client.GetAllClientsUseCase;
import com.manager.truck.truck.usecase.client.GetClientByIdUseCase;
import com.manager.truck.truck.usecase.client.SaveClientUseCase;
import com.manager.truck.truck.usecase.client.UpdateClientStatusUseCase;
import com.manager.truck.truck.usecase.client.UpdateClientUseCase;
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

@WebMvcTest(controllers = ClientController.class)
@Import(SecurityConfig.class)
class ClientSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllClientsUseCase getAllClientsUseCase;
    @MockitoBean
    private GetClientByIdUseCase getClientByIdUseCase;
    @MockitoBean
    private SaveClientUseCase saveClientUseCase;
    @MockitoBean
    private UpdateClientUseCase updateClientUseCase;
    @MockitoBean
    private UpdateClientStatusUseCase updateClientStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void listarClientesSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/clients"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarClientesConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllClientsUseCase.execute(anyLong(), any(), any()))
                .thenReturn(new ClientsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/clients").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarClientesConSuperAdminDeberiaDevolver200() throws Exception {
        when(getAllClientsUseCase.execute(anyLong(), any(), any()))
                .thenReturn(new ClientsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/clients").with(withRole("SUPER_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void listarClientesConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/clients").with(withRole("PARTNER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarClientesConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/clients").with(withRole("DRIVER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearClienteConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/clients").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientName\":\"Cliente\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearClienteConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/clients").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientName\":\"Cliente\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearClienteConSecretaryDeberiaDevolver201() throws Exception {
        ClientResponse response = new ClientResponse();
        response.setClientId(9L);
        when(saveClientUseCase.execute(anyLong(), any(ClientRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/clients").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientName\":\"Cliente Nuevo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value(9));
    }

    @Test
    void crearClienteSinNombreDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/clients").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void cambiarEstadoClienteConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/clients/9/status").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientState\":false}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoClienteConSecretaryDeberiaDevolver200() throws Exception {
        ClientResponse response = new ClientResponse();
        response.setClientId(9L);
        when(updateClientStatusUseCase.execute(anyLong(), anyLong(), any(ClientStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/companies/1/clients/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientState\":false}"))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstadoClienteSinValorDeberiaDevolver400() throws Exception {
        mockMvc.perform(put("/api/companies/1/clients/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
