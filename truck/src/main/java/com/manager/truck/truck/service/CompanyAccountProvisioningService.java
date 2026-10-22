package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.CompanyUserRoleId;
import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.RoleScope;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyAccountProvisioningService {

    private final UserService userService;
    private final CompanyUserService companyUserService;
    private final CompanyUserRoleService companyUserRoleService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User provision(Long companyId, String email, String password, String name, String lastname,
            String ci, String phone, String roleName) {
        requireAccountData(email, password, name, lastname);
        String normalizedEmail = email.trim();
        if (userService.existsByEmail(normalizedEmail)) {
            throw new ConflictException("El correo ya está registrado: " + normalizedEmail);
        }

        User user = new User();
        user.setUserEmail(normalizedEmail);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setUserName(name.trim());
        user.setUserLastname(lastname.trim());
        user.setUserCi(trimToNull(ci));
        user.setUserPhone(trimToNull(phone));
        user.setUserStatus(UserStatus.ACTIVE);

        try {
            User saved = userService.save(user);
            CompanyUser membership = new CompanyUser();
            membership.setId(new CompanyUserId(companyId, saved.getUserId()));
            membership.setMembershipStatus(MembershipStatus.ACTIVE);
            companyUserService.save(membership);

            Role role = roleService.findByRoleName(roleName);
            if (role.getRoleScope() != RoleScope.COMPANY) {
                throw new BadRequestException("El rol " + roleName + " no es un rol de empresa");
            }
            CompanyUserRole assignment = new CompanyUserRole();
            assignment.setId(new CompanyUserRoleId(companyId, saved.getUserId(), role.getRoleId()));
            assignment.setRoleScope(RoleScope.COMPANY);
            companyUserRoleService.save(assignment);
            return saved;
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("No se pudo crear la cuenta o su acceso a la empresa");
        }
    }

    private void requireAccountData(String email, String password, String name, String lastname) {
        if (isBlank(email) || isBlank(password) || isBlank(name) || isBlank(lastname)) {
            throw new BadRequestException("Los datos de acceso son obligatorios");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        return isBlank(value) ? null : value.trim();
    }
}
