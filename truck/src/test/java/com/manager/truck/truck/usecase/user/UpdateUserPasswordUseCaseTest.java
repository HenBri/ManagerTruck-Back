package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserPasswordRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateUserPasswordUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UpdateUserPasswordUseCase updateUserPasswordUseCase;

    private UserPasswordRequest request() {
        UserPasswordRequest request = new UserPasswordRequest();
        request.setCurrentPassword("actual123");
        request.setNewPassword("nueva12345");
        return request;
    }

    private User user(Long id, String passwordHash) {
        User user = new User();
        user.setUserId(id);
        user.setUserEmail("dueño@mail.com");
        user.setPasswordHash(passwordHash);
        user.setUserName("Due");
        user.setUserLastname("Ño");
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    @Test
    void debePermitirCambiarPasswordSoloAlDuenoDeLaCuenta() {
        when(currentUserProvider.get()).thenReturn(new CurrentUser(5L, "dueño@mail.com"));
        when(userService.findById(5L)).thenReturn(user(5L, "$2a$10$hashactual"));
        when(passwordEncoder.matches("actual123", "$2a$10$hashactual")).thenReturn(true);
        when(passwordEncoder.encode("nueva12345")).thenReturn("$2a$10$hashnuevo");
        when(userService.update(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = updateUserPasswordUseCase.execute(5L, request());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService).update(captor.capture());
        assertEquals("$2a$10$hashnuevo", captor.getValue().getPasswordHash());
        assertFalse(captor.getValue().getPasswordHash().contains("nueva12345"));
        assertEquals(5L, response.getUserId());
    }

    @Test
    void debeRechazarCambioDePasswordDeOtraCuenta() {
        when(currentUserProvider.get()).thenReturn(new CurrentUser(5L, "dueño@mail.com"));

        assertThrows(ForbiddenOperationException.class,
                () -> updateUserPasswordUseCase.execute(6L, request()));
    }

    @Test
    void debeRechazarSiLaPasswordActualNoCoincide() {
        when(currentUserProvider.get()).thenReturn(new CurrentUser(5L, "dueño@mail.com"));
        when(userService.findById(5L)).thenReturn(user(5L, "$2a$10$hashactual"));
        when(passwordEncoder.matches(eq("actual123"), eq("$2a$10$hashactual"))).thenReturn(false);

        assertThrows(ForbiddenOperationException.class,
                () -> updateUserPasswordUseCase.execute(5L, request()));
    }
}
