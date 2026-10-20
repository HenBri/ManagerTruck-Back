package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllUsersUseCaseTest {

    @Mock
    private UserService userService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private GetAllUsersUseCase getAllUsersUseCase;

    private User user(Long id, String email) {
        User user = new User();
        user.setUserId(id);
        user.setUserEmail(email);
        user.setPasswordHash("$2a$10$hashsecreto");
        user.setUserName("Nombre");
        user.setUserLastname("Apellido");
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    @Test
    void debeRechazarListadoGlobalSinSerSuperAdmin() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(false);

        assertThrows(ForbiddenOperationException.class,
                () -> getAllUsersUseCase.execute(PageRequest.of(0, 20)));

        verify(userService, never()).findAll(any());
    }

    @Test
    void debeDevolverPaginaSinExponerElPasswordHash() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(true);
        when(userService.findAll(any())).thenReturn(new PageImpl<>(
                List.of(user(1L, "a@mail.com"), user(2L, "b@mail.com")),
                PageRequest.of(0, 20), 2));

        UsersResponse response = getAllUsersUseCase.execute(PageRequest.of(0, 20));

        assertEquals(2, response.getUsers().size());
        assertEquals(2, response.getTotalElements());
        assertEquals("a@mail.com", response.getUsers().get(0).getEmail());
        verify(userService).findAll(any());
    }
}
