package com.manager.truck.truck.security;

import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserService userService;

    public CurrentUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
            throw new ForbiddenOperationException("No hay un usuario autenticado en el contexto de seguridad");
        }
        return new CurrentUser(principal.getId(), principal.getEmail());
    }

    public boolean isSuperAdmin() {
        CurrentUser currentUser = get();
        return userService.findPlatformRoleNames(currentUser.userId()).contains(SecurityRoles.SUPER_ADMIN);
    }

    public boolean isActiveMemberOf(Long companyId) {
        CurrentUser currentUser = get();
        return userService.isActiveMember(currentUser.userId(), companyId);
    }

    public boolean hasCompanyRole(Long companyId, String roleName) {
        CurrentUser currentUser = get();
        return userService.findCompanyRoleNames(currentUser.userId(), companyId).contains(roleName);
    }

    public void requireCompanyAdmin(Long companyId) {
        if (!isSuperAdmin() && !hasCompanyRole(companyId, SecurityRoles.SECRETARY)) {
            throw new ForbiddenOperationException(
                    "Solo SUPER_ADMIN o un SECRETARY de la empresa puede administrar sus membresías y roles");
        }
    }

    public void requireCompanyAccess(Long companyId) {
        if (!isSuperAdmin() && !isActiveMemberOf(companyId)) {
            throw new ForbiddenOperationException(
                    "No perteneces a esta empresa o tu membresía no está activa");
        }
    }
}
