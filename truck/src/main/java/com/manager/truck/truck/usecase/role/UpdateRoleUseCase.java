package com.manager.truck.truck.usecase.role;

import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.dto.request.RoleUpdateRequest;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateRoleUseCase {

    private final RoleService roleService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public RoleResponse execute(Integer roleId, RoleUpdateRequest request) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede modificar roles");
        }
        Role role = roleService.findById(roleId);
        if (!role.getRoleName().equals(request.getName()) && roleService.existsByRoleName(request.getName())) {
            throw new ConflictException("Ya existe un rol con el nombre: " + request.getName());
        }
        role.setRoleName(request.getName());
        role.setRoleDescription(request.getDescription());
        try {
            return RoleResponse.from(roleService.update(role));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Ya existe un rol con el nombre: " + request.getName());
        }
    }
}
