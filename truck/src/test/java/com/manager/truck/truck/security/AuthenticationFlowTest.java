package com.manager.truck.truck.security;

import com.manager.truck.truck.controller.UserController;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.usecase.companyuser.GetMyMembershipsUseCase;
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
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {UserController.class}, properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration"
})
@Import({SecurityConfig.class, AuthenticationFlowTest.TestAuthConfig.class})
class AuthenticationFlowTest {

    private static final String EMAIL = "miembro@mail.com";
    private static final String INACTIVE_EMAIL = "inactivo@mail.com";
    private static final String PASSWORD = "Secreta123";
    private static final BCryptPasswordEncoder HASH = new BCryptPasswordEncoder();

    @TestConfiguration
    static class TestAuthConfig {

        @Bean
        UserDetailsService testUserDetailsService() {
            String hash = HASH.encode(PASSWORD);
            return username -> {
                if (EMAIL.equals(username)) {
                    return new UserPrincipal(10L, EMAIL, hash, UserStatus.ACTIVE,
                            List.of(new SimpleGrantedAuthority("ROLE_PARTNER")));
                }
                if (INACTIVE_EMAIL.equals(username)) {
                    return new UserPrincipal(11L, INACTIVE_EMAIL, hash, UserStatus.INACTIVE, List.of());
                }
                throw new UsernameNotFoundException("Usuario no encontrado: " + username);
            };
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetCurrentUserUseCase getCurrentUserUseCase;
    @MockitoBean
    private GetMyMembershipsUseCase getMyMembershipsUseCase;
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

    @Test
    void loginValidoDeberiaDevolver200() throws Exception {
        when(getCurrentUserUseCase.execute()).thenReturn(new UserResponse());

        mockMvc.perform(get("/api/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(EMAIL, PASSWORD)))
                .andExpect(status().isOk());
    }

    @Test
    void loginConPasswordIncorrectaDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(EMAIL, "otraPassword")))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("UNAUTHORIZED"));
    }

    @Test
    void loginConEmailInexistenteDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic("nadie@mail.com", PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void loginConCuentaInactivaDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(INACTIVE_EMAIL, PASSWORD)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"));
    }

    @Test
    void peticionSinCredencialesDeberiaDevolver401() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("401"))
                .andExpect(jsonPath("$.message").value("UNAUTHORIZED"));
    }

    @Test
    void loginValidoConPasswordHasheadaNoDebeExponerElHash() throws Exception {
        when(getCurrentUserUseCase.execute()).thenReturn(new UserResponse());

        mockMvc.perform(get("/api/users/me")
                        .with(SecurityMockMvcRequestPostProcessors.httpBasic(EMAIL, PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}
