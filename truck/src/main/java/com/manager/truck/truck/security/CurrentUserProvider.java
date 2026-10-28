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

    public boolean isAdmin() {
        CurrentUser currentUser = get();
        return userService.findRoleNames(currentUser.userId()).contains(SecurityRoles.ADMIN);
    }

    public boolean isSecretary() {
        CurrentUser currentUser = get();
        return userService.findRoleNames(currentUser.userId()).contains(SecurityRoles.SECRETARY);
    }

    public boolean isPartner() {
        CurrentUser currentUser = get();
        return userService.findRoleNames(currentUser.userId()).contains(SecurityRoles.PARTNER);
    }

    public boolean isDriver() {
        CurrentUser currentUser = get();
        return userService.findRoleNames(currentUser.userId()).contains(SecurityRoles.DRIVER);
    }

    public void requireAdmin() {
        if (!isAdmin()) {
            throw new ForbiddenOperationException("Solo un administrador puede realizar esta operación");
        }
    }

    public void requireAdminOrSecretary() {
        if (!isAdmin() && !isSecretary()) {
            throw new ForbiddenOperationException("Solo un administrador o secretaría puede realizar esta operación");
        }
    }
}
