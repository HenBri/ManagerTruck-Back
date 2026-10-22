package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.enums.RoleScope;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyAccountProvisioningServiceTest {

    @Mock private UserService userService;
    @Mock private CompanyUserService companyUserService;
    @Mock private CompanyUserRoleService companyUserRoleService;
    @Mock private RoleService roleService;

    @Test
    void creaCuentaMembresiaActivaYRolDeEmpresa() {
        CompanyAccountProvisioningService service = new CompanyAccountProvisioningService(userService,
                companyUserService, companyUserRoleService, roleService, new BCryptPasswordEncoder());
        User saved = new User();
        saved.setUserId(15L);
        when(userService.save(any(User.class))).thenReturn(saved);
        Role role = new Role();
        role.setRoleId(3);
        role.setRoleScope(RoleScope.COMPANY);
        when(roleService.findByRoleName("DRIVER")).thenReturn(role);

        service.provision(1L, "driver@mail.com", "password1", "Driver", "Test", "123", "70000000", "DRIVER");

        ArgumentCaptor<CompanyUser> membership = ArgumentCaptor.forClass(CompanyUser.class);
        ArgumentCaptor<CompanyUserRole> assignment = ArgumentCaptor.forClass(CompanyUserRole.class);
        verify(companyUserService).save(membership.capture());
        verify(companyUserRoleService).save(assignment.capture());
        assertEquals(1L, membership.getValue().getId().getCompanyId());
        assertEquals(15L, membership.getValue().getId().getUserId());
        assertEquals(3, assignment.getValue().getId().getRoleId());
    }
}
