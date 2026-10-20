package com.manager.truck.truck.usecase.companyuserrole;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.dto.request.CompanyUserRolesRequest;
import com.manager.truck.truck.domain.dto.response.CompanyUserRolesResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.RoleScope;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserRoleService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.RoleService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignCompanyUserRolesUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long MEMBER_USER_ID = 5L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private CompanyUserRoleService companyUserRoleService;
    @Mock
    private CompanyUserService companyUserService;
    @Mock
    private CompanyService companyService;
    @Mock
    private RoleService roleService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private AssignCompanyUserRolesUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new AssignCompanyUserRolesUseCase(companyUserRoleService, companyUserService,
                companyService, roleService, userService, currentUserProvider);
        authenticate(SECRETARY_USER_ID);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Long userId) {
        UserPrincipal principal = new UserPrincipal(userId, "cuenta@mail.com", "hash", UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private CompanyUser membership(Long companyId, Long userId, MembershipStatus status) {
        CompanyUser membership = new CompanyUser();
        membership.setId(new CompanyUserId(companyId, userId));
        membership.setMembershipStatus(status);
        return membership;
    }

    private Role role(Integer roleId, String name, RoleScope scope) {
        Role role = new Role();
        role.setRoleId(roleId);
        role.setRoleName(name);
        role.setRoleScope(scope);
        return role;
    }

    private CompanyUserRolesRequest request(String... roleNames) {
        CompanyUserRolesRequest request = new CompanyUserRolesRequest();
        request.setRoleNames(List.of(roleNames));
        return request;
    }

    @Test
    void secretariaDeEmpresaAPuedeAsignarVariosRolesEnSuEmpresa() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A))
                .thenReturn(List.of("SECRETARY", "PARTNER", "DRIVER"));
        when(companyService.findById(COMPANY_A)).thenReturn(companyA());
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.ACTIVE)));
        when(roleService.findByRoleName("PARTNER")).thenReturn(role(3, "PARTNER", RoleScope.COMPANY));
        when(roleService.findByRoleName("DRIVER")).thenReturn(role(4, "DRIVER", RoleScope.COMPANY));
        when(companyUserRoleService.existsByCompanyIdAndUserIdAndRoleId(COMPANY_A, MEMBER_USER_ID, 3))
                .thenReturn(false);
        when(companyUserRoleService.existsByCompanyIdAndUserIdAndRoleId(COMPANY_A, MEMBER_USER_ID, 4))
                .thenReturn(false);
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A))
                .thenReturn(List.of("PARTNER", "DRIVER"));

        CompanyUserRolesResponse response = useCase.execute(COMPANY_A, MEMBER_USER_ID,
                request("PARTNER", "DRIVER"));

        ArgumentCaptor<List<CompanyUserRole>> captor = ArgumentCaptor.forClass(List.class);
        verify(companyUserRoleService).saveAll(captor.capture());
        assertEquals(2, captor.getValue().size());
        assertEquals(3, captor.getValue().get(0).getId().getRoleId());
        assertEquals(4, captor.getValue().get(1).getId().getRoleId());
        assertEquals(RoleScope.COMPANY, captor.getValue().get(0).getRoleScope());
        assertEquals(List.of("PARTNER", "DRIVER"), response.getRoleNames());
    }

    @Test
    void secretariaDeEmpresaANoPuedeOperarEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, MEMBER_USER_ID, request("PARTNER")));

        verify(companyUserRoleService, never()).saveAll(anyList());
    }

    @Test
    void superAdminPuedeAsignarRolesEnCualquierEmpresa() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of("SUPER_ADMIN"));
        when(companyService.findById(COMPANY_B)).thenReturn(companyB());
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_B, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_B, MEMBER_USER_ID, MembershipStatus.ACTIVE)));
        when(roleService.findByRoleName("SECRETARY")).thenReturn(role(2, "SECRETARY", RoleScope.COMPANY));
        when(companyUserRoleService.existsByCompanyIdAndUserIdAndRoleId(COMPANY_B, MEMBER_USER_ID, 2))
                .thenReturn(false);
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_B)).thenReturn(List.of("SECRETARY"));

        CompanyUserRolesResponse response = useCase.execute(COMPANY_B, MEMBER_USER_ID, request("SECRETARY"));

        assertEquals(List.of("SECRETARY"), response.getRoleNames());
        verify(companyUserRoleService).saveAll(anyList());
    }

    @Test
    void intentoDeElevarPrivilegiosASuperAdminDeberiaSerRechazado() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A))
                .thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(companyA());
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.ACTIVE)));
        when(roleService.findByRoleName("SUPER_ADMIN")).thenReturn(role(1, "SUPER_ADMIN", RoleScope.PLATFORM));

        ForbiddenOperationException exception = assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, MEMBER_USER_ID, request("SUPER_ADMIN")));

        assertEquals(true, exception.getMessage().contains("plataforma"));
        verify(companyUserRoleService, never()).saveAll(anyList());
    }

    @Test
    void asignarRolSobreMembresiaInactivaDeberiaDevolverConflicto() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A))
                .thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(companyA());
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.INACTIVE)));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, MEMBER_USER_ID, request("PARTNER")));

        verify(companyUserRoleService, never()).saveAll(anyList());
    }

    @Test
    void asignarRolAUsuarioSinMembresiaDeberiaDevolver404() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A))
                .thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(companyA());
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, MEMBER_USER_ID, request("PARTNER")));
    }

    @Test
    void rolYaAsignadoNoDebeDuplicarse() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A))
                .thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(companyA());
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.ACTIVE)));
        when(roleService.findByRoleName("PARTNER")).thenReturn(role(3, "PARTNER", RoleScope.COMPANY));
        when(companyUserRoleService.existsByCompanyIdAndUserIdAndRoleId(COMPANY_A, MEMBER_USER_ID, 3))
                .thenReturn(true);
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));

        useCase.execute(COMPANY_A, MEMBER_USER_ID, request("PARTNER"));

        verify(companyUserRoleService, never()).saveAll(anyList());
    }

    @Test
    void usuarioNoAdminSinMembresiaDeberiaSerRechazado() {
        authenticate(MEMBER_USER_ID);
        when(userService.findPlatformRoleNames(MEMBER_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, MEMBER_USER_ID, request("PARTNER")));

        verify(companyUserRoleService, never()).saveAll(anyList());
    }

    private com.manager.truck.truck.domain.Company companyA() {
        com.manager.truck.truck.domain.Company company = new com.manager.truck.truck.domain.Company();
        company.setCompanyId(COMPANY_A);
        company.setCompanyName("Empresa A");
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    private com.manager.truck.truck.domain.Company companyB() {
        com.manager.truck.truck.domain.Company company = new com.manager.truck.truck.domain.Company();
        company.setCompanyId(COMPANY_B);
        company.setCompanyName("Empresa B");
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }
}
