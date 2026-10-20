package com.manager.truck.truck.usecase.role;

import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.dto.request.RoleRequest;
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
public class SaveRoleUseCase {

    private final RoleService roleService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public RoleResponse execute(RoleRequest request) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede crear roles");
        }
        if (roleService.existsByRoleName(request.getName())) {
            throw new ConflictException("Ya existe un rol con el nombre: " + request.getName());
        }
        Role role = new Role();
        role.setRoleName(request.getName());
        role.setRoleScope(request.getScope());
        role.setRoleDescription(request.getDescription());
        try {
            return RoleResponse.from(roleService.save(role));
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("Ya existe un rol con el nombre: " + request.getName());
        }
    }
}
