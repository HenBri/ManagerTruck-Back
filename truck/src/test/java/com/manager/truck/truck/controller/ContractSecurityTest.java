package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.request.ContractStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.contract.GetAllContractsUseCase;
import com.manager.truck.truck.usecase.contract.GetContractByIdUseCase;
import com.manager.truck.truck.usecase.contract.SaveContractUseCase;
import com.manager.truck.truck.usecase.contract.UpdateContractStatusUseCase;
import com.manager.truck.truck.usecase.contract.UpdateContractUseCase;
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

@WebMvcTest(controllers = ContractController.class)
@Import(SecurityConfig.class)
class ContractSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllContractsUseCase getAllContractsUseCase;
    @MockitoBean
    private GetContractByIdUseCase getContractByIdUseCase;
    @MockitoBean
    private SaveContractUseCase saveContractUseCase;
    @MockitoBean
    private UpdateContractUseCase updateContractUseCase;
    @MockitoBean
    private UpdateContractStatusUseCase updateContractStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    private static final String VALID_BODY =
            "{\"clientId\":1,\"contractAmount\":1500.00,\"contractDate\":\"2026-10-16\"}";

    @Test
    void listarContratosSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/contracts"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarContratosConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllContractsUseCase.execute(anyLong(), any(), any(), any()))
                .thenReturn(new ContractsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/contracts").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarContratosConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/contracts").with(withRole("PARTNER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarContratosConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/contracts").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearContratoConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/contracts").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearContratoConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/contracts").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearContratoConSecretaryDeberiaDevolver201() throws Exception {
        ContractResponse response = new ContractResponse();
        response.setContractId(7L);
        when(saveContractUseCase.execute(anyLong(), any(ContractRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/contracts").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.contractId").value(7));
    }

    @Test
    void crearContratoConMontoNegativoDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/contracts").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":1,\"contractAmount\":-0.01,\"contractDate\":\"2026-10-16\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void crearContratoSinClienteDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/contracts").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contractAmount\":100.00,\"contractDate\":\"2026-10-16\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crearContratoConFechaInvalidaDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/contracts").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clientId\":1,\"contractAmount\":100.00,\"contractDate\":\"no-date\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void cambiarEstadoContratoConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/contracts/9/status").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoContratoConSecretaryDeberiaDevolver200() throws Exception {
        ContractResponse response = new ContractResponse();
        response.setContractId(9L);
        when(updateContractStatusUseCase.execute(anyLong(), anyLong(), any(ContractStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/companies/1/contracts/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstadoAValorInvalidoDeberiaDevolver400() throws Exception {
        mockMvc.perform(put("/api/companies/1/contracts/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INVENTADO\"}"))
                .andExpect(status().isBadRequest());
    }
}
