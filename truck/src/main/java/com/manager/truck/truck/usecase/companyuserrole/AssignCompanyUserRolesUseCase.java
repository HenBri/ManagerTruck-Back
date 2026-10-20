package com.manager.truck.truck.usecase.companyuserrole;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.CompanyUserRoleId;
import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.dto.request.CompanyUserRolesRequest;
import com.manager.truck.truck.domain.dto.response.CompanyUserRolesResponse;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.RoleScope;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserRoleService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.RoleService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignCompanyUserRolesUseCase {

    private final CompanyUserRoleService companyUserRoleService;
    private final CompanyUserService companyUserService;
    private final CompanyService companyService;
    private final RoleService roleService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public CompanyUserRolesResponse execute(Long companyId, Long userId, CompanyUserRolesRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        CompanyUser membership = companyUserService
                .findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "El usuario " + userId + " no es miembro de la empresa " + companyId));
        if (membership.getMembershipStatus() != MembershipStatus.ACTIVE) {
            throw new ConflictException("No se pueden asignar roles sobre una membresía INACTIVE");
        }
        List<CompanyUserRole> newAssignments = new ArrayList<>();
        for (String roleName : request.getRoleNames()) {
            Role role = roleService.findByRoleName(roleName);
            if (role.getRoleScope() != RoleScope.COMPANY) {
                throw new ForbiddenOperationException(
                        "El rol '" + roleName + "' es de plataforma y no puede asignarse a una empresa");
            }
            if (!companyUserRoleService.existsByCompanyIdAndUserIdAndRoleId(companyId, userId, role.getRoleId())) {
                CompanyUserRole assignment = new CompanyUserRole();
                assignment.setId(new CompanyUserRoleId(companyId, userId, role.getRoleId()));
                assignment.setRoleScope(RoleScope.COMPANY);
                newAssignments.add(assignment);
            }
        }
        if (!newAssignments.isEmpty()) {
            companyUserRoleService.saveAll(newAssignments);
        }
        return new CompanyUserRolesResponse(companyId, userId,
                userService.findCompanyRoleNames(userId, companyId));
    }
}
