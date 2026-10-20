package com.manager.truck.truck.usecase.companyuserrole;

import com.manager.truck.truck.domain.dto.response.CompanyUserRolesResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCompanyUserRolesUseCase {

    private final CompanyUserService companyUserService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public CompanyUserRolesResponse execute(Long companyId, Long userId) {
        CurrentUser currentUser = currentUserProvider.get();
        boolean isAdmin = currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
        boolean isSelfActiveMember = currentUser.userId().equals(userId)
                && currentUserProvider.isActiveMemberOf(companyId);
        if (!isAdmin && !isSelfActiveMember) {
            throw new ForbiddenOperationException(
                    "Solo SUPER_ADMIN, un SECRETARY de la empresa o el propio usuario puede consultar estos roles");
        }
        if (companyUserService.findByCompanyIdAndUserId(companyId, userId).isEmpty()) {
            throw new ResourceNotFoundException(
                    "Membresía no encontrada para el usuario " + userId + " en la empresa " + companyId);
        }
        List<String> roleNames = userService.findCompanyRoleNames(userId, companyId);
        return new CompanyUserRolesResponse(companyId, userId, roleNames);
    }
}
