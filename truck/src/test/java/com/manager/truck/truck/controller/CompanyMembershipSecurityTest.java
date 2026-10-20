package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.CompanyMembershipRequest;
import com.manager.truck.truck.domain.dto.request.CompanyMembershipStatusRequest;
import com.manager.truck.truck.domain.dto.request.CompanyUserRolesRequest;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipsResponse;
import com.manager.truck.truck.domain.dto.response.CompanyUserRolesResponse;
import com.manager.truck.truck.domain.dto.response.MyMembershipsResponse;
import com.manager.truck.truck.domain.dto.response.PlatformUserRolesResponse;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.companyuser.GetCompanyMembershipsUseCase;
import com.manager.truck.truck.usecase.companyuser.GetMyMembershipsUseCase;
import com.manager.truck.truck.usecase.companyuser.SaveCompanyUserUseCase;
import com.manager.truck.truck.usecase.companyuser.UpdateCompanyMembershipStatusUseCase;
import com.manager.truck.truck.usecase.companyuserrole.AssignCompanyUserRolesUseCase;
import com.manager.truck.truck.usecase.companyuserrole.GetCompanyUserRolesUseCase;
import com.manager.truck.truck.usecase.platformuserrole.GetPlatformUserRolesUseCase;
import com.manager.truck.truck.usecase.user.GetAllUsersUseCase;
import com.manager.truck.truck.usecase.user.GetCurrentUserUseCase;
import com.manager.truck.truck.usecase.user.GetUserByIdUseCase;
import com.manager.truck.truck.usecase.user.RegisterUserUseCase;
import com.manager.truck.truck.usecase.user.UpdateUserPasswordUseCase;
import com.manager.truck.truck.usecase.user.UpdateUserStatusUseCase;
import com.manager.truck.truck.usecase.user.UpdateUserUseCase;
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

@WebMvcTest(controllers = {CompanyMembershipController.class, PlatformUserRoleController.class, UserController.class})
@Import(SecurityConfig.class)
class CompanyMembershipSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCompanyMembershipsUseCase getCompanyMembershipsUseCase;
    @MockitoBean
    private SaveCompanyUserUseCase saveCompanyUserUseCase;
    @MockitoBean
    private UpdateCompanyMembershipStatusUseCase updateCompanyMembershipStatusUseCase;
    @MockitoBean
    private GetCompanyUserRolesUseCase getCompanyUserRolesUseCase;
    @MockitoBean
    private AssignCompanyUserRolesUseCase assignCompanyUserRolesUseCase;
    @MockitoBean
    private GetPlatformUserRolesUseCase getPlatformUserRolesUseCase;
    @MockitoBean
    private GetMyMembershipsUseCase getMyMembershipsUseCase;
    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;
    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;
    @MockitoBean
    private GetUserByIdUseCase getUserByIdUseCase;
    @MockitoBean
    private UpdateUserUseCase updateUserUseCase;
    @MockitoBean
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;
    @MockitoBean
    private UpdateUserStatusUseCase updateUserStatusUseCase;
    @MockitoBean
    private GetAllUsersUseCase getAllUsersUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    // --- Listado de membresías ---

    @Test
    void listarMembresiasSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/memberships"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void listarMembresiasConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/memberships").with(withRole("PARTNER")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarMembresiasConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/memberships").with(withRole("DRIVER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void listarMembresiasConSecretaryDeberiaDevolver200() throws Exception {
        when(getCompanyMembershipsUseCase.execute(anyLong(), any()))
                .thenReturn(new CompanyMembershipsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/memberships").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarMembresiasConSuperAdminDeberiaDevolver200() throws Exception {
        when(getCompanyMembershipsUseCase.execute(anyLong(), any()))
                .thenReturn(new CompanyMembershipsResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/memberships").with(withRole("SUPER_ADMIN")))
                .andExpect(status().isOk());
    }

    // --- Alta de membresía ---

    @Test
    void crearMembresiaSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(post("/api/companies/1/memberships")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"nuevo@mail.com\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearMembresiaConDriverDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies/1/memberships")
                        .with(withRole("DRIVER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"nuevo@mail.com\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearMembresiaConSecretaryDeberiaDevolver201() throws Exception {
        when(saveCompanyUserUseCase.execute(anyLong(), any(CompanyMembershipRequest.class)))
                .thenReturn(new CompanyMembershipResponse());

        mockMvc.perform(post("/api/companies/1/memberships")
                        .with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userEmail\":\"nuevo@mail.com\"}"))
                .andExpect(status().isCreated());
    }

    // --- Cambio de estado de membresía ---

    @Test
    void cambiarEstadoMembresiaSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(put("/api/companies/1/memberships/5/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cambiarEstadoMembresiaConPartnerDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/companies/1/memberships/5/status")
                        .with(withRole("PARTNER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void cambiarEstadoMembresiaConSecretaryDeberiaDevolver200() throws Exception {
        when(updateCompanyMembershipStatusUseCase.execute(anyLong(), anyLong(),
                any(CompanyMembershipStatusRequest.class))).thenReturn(new CompanyMembershipResponse());

        mockMvc.perform(put("/api/companies/1/memberships/5/status")
                        .with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"INACTIVE\"}"))
                .andExpect(status().isOk());
    }

    // --- Asignación de roles de empresa ---

    @Test
    void asignarRolesSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(post("/api/companies/1/memberships/5/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleNames\":[\"PARTNER\"]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void asignarRolesConSecretariaDeberiaDevolver200() throws Exception {
        when(assignCompanyUserRolesUseCase.execute(anyLong(), anyLong(), any(CompanyUserRolesRequest.class)))
                .thenReturn(new CompanyUserRolesResponse(1L, 5L, List.of("PARTNER", "DRIVER")));

        mockMvc.perform(post("/api/companies/1/memberships/5/roles")
                        .with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roleNames\":[\"PARTNER\",\"DRIVER\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roleNames[0]").value("PARTNER"))
                .andExpect(jsonPath("$.roleNames[1]").value("DRIVER"));
    }

    @Test
    void consultarRolesDeMiembroSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies/1/memberships/5/roles"))
                .andExpect(status().isUnauthorized());
    }

    // --- Roles de plataforma (solo SUPER_ADMIN) ---

    @Test
    void listarRolesDePlataformaSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/platform/user-roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarRolesDePlataformaConSecretaryDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/platform/user-roles").with(withRole("SECRETARY")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarRolesDePlataformaConSuperAdminDeberiaDevolver200() throws Exception {
        when(getPlatformUserRolesUseCase.execute()).thenReturn(new PlatformUserRolesResponse(List.of()));

        mockMvc.perform(get("/api/platform/user-roles").with(withRole("SUPER_ADMIN")))
                .andExpect(status().isOk());
    }

    // --- Membresías propias ---

    @Test
    void listarMisMembresiasSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users/me/memberships"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarMisMembresiasAutenticadoDeberiaDevolver200() throws Exception {
        when(getMyMembershipsUseCase.execute()).thenReturn(new MyMembershipsResponse(List.of()));

        mockMvc.perform(get("/api/users/me/memberships").with(withRole("DRIVER")))
                .andExpect(status().isOk());
    }
}
