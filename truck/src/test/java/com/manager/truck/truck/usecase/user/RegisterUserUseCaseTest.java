package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRegisterRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private UserRegisterRequest request() {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("nuevo@mail.com");
        request.setPassword("secreta123");
        request.setName("Nuevo");
        request.setLastname("Usuario");
        request.setCi("1234567890");
        request.setPhone("099999999");
        return request;
    }

    @Test
    void debeHashearLaPasswordYCrearLaCuentaActiva() {
        when(userService.existsByEmail("nuevo@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secreta123")).thenReturn("$2a$10$hashgenerado");
        when(userService.save(any(User.class))).thenAnswer(invocation -> {
            User saved = invocation.getArgument(0);
            saved.setUserId(10L);
            return saved;
        });
        when(userService.findById(10L)).thenAnswer(invocation -> {
            User user = new User();
            user.setUserId(10L);
            user.setUserEmail("nuevo@mail.com");
            user.setPasswordHash("$2a$10$hashgenerado");
            user.setUserName("Nuevo");
            user.setUserLastname("Usuario");
            user.setUserStatus(UserStatus.ACTIVE);
            return user;
        });

        UserResponse response = registerUserUseCase.execute(request());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).save(captor.capture());
        User persisted = captor.getValue();
        assertEquals("$2a$10$hashgenerado", persisted.getPasswordHash());
        assertFalse(persisted.getPasswordHash().contains("secreta123"));
        assertEquals(UserStatus.ACTIVE, persisted.getUserStatus());
        assertEquals("nuevo@mail.com", persisted.getUserEmail());
        assertNotNull(response);
        assertEquals(10L, response.getUserId());
        assertEquals(UserStatus.ACTIVE, response.getStatus());
    }

    @Test
    void debeRechazarCorreoDuplicadoCon409() {
        when(userService.existsByEmail("nuevo@mail.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> registerUserUseCase.execute(request()));

        verify(userService, never()).save(any());
        verify(passwordEncoder, never()).encode(anyString());
    }
}
