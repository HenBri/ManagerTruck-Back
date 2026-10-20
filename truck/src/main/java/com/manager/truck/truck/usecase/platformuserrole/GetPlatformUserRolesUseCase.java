package com.manager.truck.truck.usecase.platformuserrole;

import com.manager.truck.truck.domain.PlatformUserRole;
import com.manager.truck.truck.domain.dto.response.PlatformUserRoleResponse;
import com.manager.truck.truck.domain.dto.response.PlatformUserRolesResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.PlatformUserRoleService;
import com.manager.truck.truck.service.RoleService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPlatformUserRolesUseCase {

    private final PlatformUserRoleService platformUserRoleService;
    private final UserService userService;
    private final RoleService roleService;
    private final CurrentUserProvider currentUserProvider;

    public PlatformUserRolesResponse execute() {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede consultar los roles de plataforma");
        }
        List<PlatformUserRoleResponse> grants = platformUserRoleService.findAll().stream()
                .map(this::toResponse)
                .toList();
        return new PlatformUserRolesResponse(grants);
    }

    private PlatformUserRoleResponse toResponse(PlatformUserRole grant) {
        var user = userService.findById(grant.getId().getUserId());
        var role = roleService.findById(grant.getId().getRoleId());
        return PlatformUserRoleResponse.of(user.getUserId(), user.getUserEmail(),
                role.getRoleName(), grant.getGrantedAt());
    }
}
