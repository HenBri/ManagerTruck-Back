package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.request.ChargeStatusRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.dto.response.ChargesResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.charge.GetAllChargesUseCase;
import com.manager.truck.truck.usecase.charge.GetChargeByIdUseCase;
import com.manager.truck.truck.usecase.charge.SaveChargeUseCase;
import com.manager.truck.truck.usecase.charge.UpdateChargeStatusUseCase;
import com.manager.truck.truck.usecase.charge.UpdateChargeUseCase;
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

@WebMvcTest(controllers = ChargeController.class)
@Import(SecurityConfig.class)
class ChargeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllChargesUseCase getAllChargesUseCase;
    @MockitoBean
    private GetChargeByIdUseCase getChargeByIdUseCase;
    @MockitoBean
    private SaveChargeUseCase saveChargeUseCase;
    @MockitoBean
    private UpdateChargeUseCase updateChargeUseCase;
    @MockitoBean
    private UpdateChargeStatusUseCase updateChargeStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void listarCargasSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/charges"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarCargasConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllChargesUseCase.execute(anyLong(), any(), any(), any()))
                .thenReturn(new ChargesResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/charges").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarCargasConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/charges").with(withRole("PARTNER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarCargasConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/charges").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearCargaConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/charges").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contractId\":1,\"chargeDescription\":\"Carga\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearCargaConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/charges").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contractId\":1,\"chargeDescription\":\"Carga\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void crearCargaConSecretaryDeberiaDevolver201() throws Exception {
        ChargeResponse response = new ChargeResponse();
        response.setChargeId(11L);
        when(saveChargeUseCase.execute(anyLong(), any(ChargeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/companies/1/charges").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"contractId\":1,\"chargeDescription\":\"Carga de prueba\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.chargeId").value(11));
    }

    @Test
    void crearCargaSinContratoDeberiaDevolver400() throws Exception {
        mockMvc.perform(post("/api/companies/1/charges").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"chargeDescription\":\"Carga\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    @Test
    void cambiarEstadoCargaConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/charges/9/status").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_TRANSIT\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoCargaConSecretaryDeberiaDevolver200() throws Exception {
        ChargeResponse response = new ChargeResponse();
        response.setChargeId(9L);
        when(updateChargeStatusUseCase.execute(anyLong(), anyLong(), any(ChargeStatusRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/companies/1/charges/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"IN_TRANSIT\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void cambiarEstadoAValorInvalidoDeberiaDevolver400() throws Exception {
        mockMvc.perform(put("/api/companies/1/charges/9/status").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"VOLANDO\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaDeCargaPorIdConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/charges/9").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }
}
