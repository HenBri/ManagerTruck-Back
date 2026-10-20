package com.manager.truck.truck.usecase.role;

import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.dto.request.RoleRequest;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.domain.enums.RoleScope;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.RoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveRoleUseCaseTest {

    @Mock
    private RoleService roleService;

    @Mock
    private CurrentUserProvider currentUserProvider;

    @InjectMocks
    private SaveRoleUseCase saveRoleUseCase;

    private RoleRequest request() {
        RoleRequest request = new RoleRequest();
        request.setName("INSPECTOR");
        request.setScope(RoleScope.COMPANY);
        request.setDescription("Rol de inspeccion");
        return request;
    }

    @Test
    void debeRechazarCreacionDeRolSinSerSuperAdmin() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> saveRoleUseCase.execute(request()));

        verify(roleService, never()).save(any());
    }

    @Test
    void debeRechazarNombreDeRolDuplicadoCon409() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(true);
        when(roleService.existsByRoleName("INSPECTOR")).thenReturn(true);

        assertThrows(ConflictException.class, () -> saveRoleUseCase.execute(request()));

        verify(roleService, never()).save(any());
    }

    @Test
    void debeCrearRolConSuScope() {
        when(currentUserProvider.isSuperAdmin()).thenReturn(true);
        when(roleService.existsByRoleName("INSPECTOR")).thenReturn(false);
        when(roleService.save(any(Role.class))).thenAnswer(invocation -> {
            Role role = invocation.getArgument(0);
            role.setRoleId(9);
            return role;
        });

        RoleResponse response = saveRoleUseCase.execute(request());

        assertEquals(9, response.getRoleId());
        assertEquals("INSPECTOR", response.getName());
        assertEquals(RoleScope.COMPANY, response.getScope());
    }
}
