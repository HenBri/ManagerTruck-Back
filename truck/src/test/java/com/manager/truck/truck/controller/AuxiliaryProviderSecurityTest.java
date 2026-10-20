package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProvidersResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.auxiliaryprovider.GetAllAuxiliaryProvidersUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.GetAuxiliaryProviderByIdUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.SaveAuxiliaryProviderUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.UpdateAuxiliaryProviderStatusUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.UpdateAuxiliaryProviderUseCase;
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

@WebMvcTest(controllers = AuxiliaryProviderController.class)
@Import(SecurityConfig.class)
class AuxiliaryProviderSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetAllAuxiliaryProvidersUseCase getAllAuxiliaryProvidersUseCase;
    @MockitoBean
    private GetAuxiliaryProviderByIdUseCase getAuxiliaryProviderByIdUseCase;
    @MockitoBean
    private SaveAuxiliaryProviderUseCase saveAuxiliaryProviderUseCase;
    @MockitoBean
    private UpdateAuxiliaryProviderUseCase updateAuxiliaryProviderUseCase;
    @MockitoBean
    private UpdateAuxiliaryProviderStatusUseCase updateAuxiliaryProviderStatusUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void listarProveedoresSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/auxiliary-providers"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void crearProveedorSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(post("/api/companies/1/auxiliary-providers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerKind\":\"BUSINESS\",\"providerName\":\"Aux\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarProveedoresConSecretaryDeberiaDevolver200() throws Exception {
        when(getAllAuxiliaryProvidersUseCase.execute(anyLong(), any(), any(), any(), any()))
                .thenReturn(new AuxiliaryProvidersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/auxiliary-providers").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarProveedoresConSuperAdminDeberiaDevolver200() throws Exception {
        when(getAllAuxiliaryProvidersUseCase.execute(anyLong(), any(), any(), any(), any()))
                .thenReturn(new AuxiliaryProvidersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/auxiliary-providers").with(withRole("SUPER_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void listarProveedoresConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/auxiliary-providers").with(withRole("PARTNER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarProveedoresConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/auxiliary-providers").with(withRole("DRIVER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearProveedorConSecretaryDeberiaDevolver201() throws Exception {
        AuxiliaryProviderResponse response = new AuxiliaryProviderResponse();
        response.setProviderId(9L);
        when(saveAuxiliaryProviderUseCase.execute(anyLong(), any(AuxiliaryProviderRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/companies/1/auxiliary-providers").with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerKind\":\"BUSINESS\",\"providerName\":\"Aux\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.providerId").value(9));
    }

    @Test
    void crearProveedorConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/auxiliary-providers").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerKind\":\"BUSINESS\",\"providerName\":\"Aux\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearProveedorConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/auxiliary-providers").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerKind\":\"BUSINESS\",\"providerName\":\"Aux\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizarProveedorConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/auxiliary-providers/9").with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"providerKind\":\"BUSINESS\",\"providerName\":\"Aux\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoProveedorConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/auxiliary-providers/9/status").with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void verProveedorPorIdConSecretaryDeberiaDevolver200() throws Exception {
        AuxiliaryProviderResponse response = new AuxiliaryProviderResponse();
        response.setProviderId(9L);
        when(getAuxiliaryProviderByIdUseCase.execute(anyLong(), anyLong())).thenReturn(response);

        mockMvc.perform(get("/api/companies/1/auxiliary-providers/9").with(withRole("SECRETARY")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerId").value(9));
    }
}
