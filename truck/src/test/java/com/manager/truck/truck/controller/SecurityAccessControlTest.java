package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.request.RoleUpdateRequest;
import com.manager.truck.truck.domain.dto.request.UserRegisterRequest;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.domain.dto.response.RolesResponse;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.security.SecurityConfig;
import com.manager.truck.truck.usecase.company.GetAllCompaniesUseCase;
import com.manager.truck.truck.usecase.company.GetCompanyByIdUseCase;
import com.manager.truck.truck.usecase.company.SaveCompanyUseCase;
import com.manager.truck.truck.usecase.company.UpdateCompanyStatusUseCase;
import com.manager.truck.truck.usecase.company.UpdateCompanyUseCase;
import com.manager.truck.truck.usecase.role.GetAllRolesUseCase;
import com.manager.truck.truck.usecase.role.GetRoleByIdUseCase;
import com.manager.truck.truck.usecase.role.SaveRoleUseCase;
import com.manager.truck.truck.usecase.role.UpdateRoleUseCase;
import com.manager.truck.truck.usecase.user.GetAllUsersUseCase;
import com.manager.truck.truck.usecase.user.GetCurrentUserUseCase;
import com.manager.truck.truck.usecase.user.GetUserByIdUseCase;
import com.manager.truck.truck.usecase.user.GetCompanyUsersUseCase;
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

@WebMvcTest(controllers = {CompanyController.class, UserController.class, RoleController.class})
@Import(SecurityConfig.class)
class SecurityAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SaveCompanyUseCase saveCompanyUseCase;
    @MockitoBean
    private GetAllCompaniesUseCase getAllCompaniesUseCase;
    @MockitoBean
    private GetCompanyByIdUseCase getCompanyByIdUseCase;
    @MockitoBean
    private UpdateCompanyUseCase updateCompanyUseCase;
    @MockitoBean
    private UpdateCompanyStatusUseCase updateCompanyStatusUseCase;
    @MockitoBean
    private GetCompanyUsersUseCase getCompanyUsersUseCase;
    @MockitoBean
    private RegisterUserUseCase registerUserUseCase;
    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;
    @MockitoBean
    private com.manager.truck.truck.usecase.companyuser.GetMyMembershipsUseCase getMyMembershipsUseCase;
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
    @MockitoBean
    private GetAllRolesUseCase getAllRolesUseCase;
    @MockitoBean
    private GetRoleByIdUseCase getRoleByIdUseCase;
    @MockitoBean
    private SaveRoleUseCase saveRoleUseCase;
    @MockitoBean
    private UpdateRoleUseCase updateRoleUseCase;

    private static SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor withRole(String role) {
        return SecurityMockMvcRequestPostProcessors.user("tester")
                .authorities(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Test
    void empresasSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void crearEmpresaConRolNoAdminDeberiaDevolver403() throws Exception {
        mockMvc.perform(post("/api/companies")
                        .with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme\"}"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void crearEmpresaConSuperAdminDeberiaDevolver201() throws Exception {
        when(saveCompanyUseCase.execute(any(CompanyRequest.class))).thenReturn(new CompanyResponse());

        mockMvc.perform(post("/api/companies")
                        .with(withRole("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Acme\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void registroDeUsuarioDeberiaSerPublico() throws Exception {
        when(registerUserUseCase.execute(any(UserRegisterRequest.class))).thenReturn(new UserResponse());

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nuevo@mail.com\",\"password\":\"secreta123\",\"name\":\"Nuevo\",\"lastname\":\"Usuario\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void listarUsuariosSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarUsuariosConRolNoAdminDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/users").with(withRole("SECRETARY")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("403"));
    }

    @Test
    void listarUsuariosConSuperAdminDeberiaDevolver200() throws Exception {
        when(getAllUsersUseCase.execute(any())).thenReturn(new UsersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/users").with(withRole("SUPER_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void verMiembrosDeEmpresaConRolNoPermitidoDeberiaDevolver403() throws Exception {
        mockMvc.perform(get("/api/companies/1/users").with(withRole("PARTNER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void verMiembrosDeEmpresaConSecretaryDeberiaDevolver200() throws Exception {
        when(getCompanyUsersUseCase.execute(anyLong(), any()))
                .thenReturn(new UsersResponse(List.of(), 0, 20, 0, 0));

        mockMvc.perform(get("/api/companies/1/users").with(withRole("SECRETARY")))
                .andExpect(status().isOk());
    }

    @Test
    void listarRolesSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listarRolesAutenticadoDeberiaDevolver200() throws Exception {
        when(getAllRolesUseCase.execute()).thenReturn(new RolesResponse(List.of()));

        mockMvc.perform(get("/api/roles").with(withRole("DRIVER")))
                .andExpect(status().isOk());
    }

    @Test
    void actualizarRolSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(put("/api/roles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"DRIVER\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void actualizarRolConRolNoAdminDeberiaDevolver403() throws Exception {
        mockMvc.perform(put("/api/roles/1")
                        .with(withRole("SECRETARY"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"DRIVER\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void actualizarRolConSuperAdminDeberiaDevolver200() throws Exception {
        when(updateRoleUseCase.execute(any(), any(RoleUpdateRequest.class))).thenReturn(new RoleResponse());

        mockMvc.perform(put("/api/roles/1")
                        .with(withRole("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"DRIVER\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void perfilSinAutenticacionDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
