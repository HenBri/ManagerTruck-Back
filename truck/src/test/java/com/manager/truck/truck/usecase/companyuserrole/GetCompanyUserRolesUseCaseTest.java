package com.manager.truck.truck.usecase.companyuserrole;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.dto.response.CompanyUserRolesResponse;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCompanyUserRolesUseCaseTest {

    private static final Long MEMBER_USER_ID = 5L;
    private static final Long OTHER_USER_ID = 6L;
    private static final Long COMPANY_A = 1L;

    @Mock
    private CompanyUserService companyUserService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private GetCompanyUserRolesUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetCompanyUserRolesUseCase(companyUserService, userService, currentUserProvider);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Long userId) {
        UserPrincipal principal = new UserPrincipal(userId, "cuenta" + userId + "@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private CompanyUser activeMembership() {
        CompanyUser membership = new CompanyUser();
        membership.setId(new CompanyUserId(COMPANY_A, MEMBER_USER_ID));
        membership.setMembershipStatus(MembershipStatus.ACTIVE);
        return membership;
    }

    @Test
    void elPropioMiembroPuedeVerSusRolesEnSuEmpresa() {
        authenticate(MEMBER_USER_ID);
        when(userService.findPlatformRoleNames(MEMBER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(MEMBER_USER_ID, COMPANY_A)).thenReturn(true);
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(activeMembership()));
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A))
                .thenReturn(List.of("PARTNER", "DRIVER"));

        CompanyUserRolesResponse response = useCase.execute(COMPANY_A, MEMBER_USER_ID);

        assertEquals(List.of("PARTNER", "DRIVER"), response.getRoleNames());
        assertEquals(COMPANY_A, response.getCompanyId());
    }

    @Test
    void unUsuarioNoAdminNoPuedeVerLosRolesDeOtroUsuario() {
        authenticate(MEMBER_USER_ID);
        when(userService.findPlatformRoleNames(MEMBER_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, OTHER_USER_ID));
    }

    @Test
    void unMiembroInactivoNoPuedeConsultarRolesNiAunSiMismo() {
        authenticate(MEMBER_USER_ID);
        when(userService.findPlatformRoleNames(MEMBER_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of());
        when(userService.isActiveMember(MEMBER_USER_ID, COMPANY_A)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, MEMBER_USER_ID));
    }

    @Test
    void secretariaDeLaEmpresaPuedeVerRolesDeCualquierMiembro() {
        authenticate(MEMBER_USER_ID);
        when(userService.findPlatformRoleNames(MEMBER_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, OTHER_USER_ID))
                .thenReturn(Optional.of(activeMembership(OTHER_USER_ID)));
        when(userService.findCompanyRoleNames(OTHER_USER_ID, COMPANY_A)).thenReturn(List.of("DRIVER"));

        CompanyUserRolesResponse response = useCase.execute(COMPANY_A, OTHER_USER_ID);

        assertEquals(List.of("DRIVER"), response.getRoleNames());
    }

    @Test
    void superAdminPuedeVerRolesDeCualquierMiembroSinSerMiembro() {
        authenticate(99L);
        when(userService.findPlatformRoleNames(99L)).thenReturn(List.of("SUPER_ADMIN"));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(activeMembership()));
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));

        CompanyUserRolesResponse response = useCase.execute(COMPANY_A, MEMBER_USER_ID);

        assertEquals(List.of("SECRETARY"), response.getRoleNames());
    }

    @Test
    void consultorAutorizadoSobreUsuarioSinMembresiaDeberiaDevolver404() {
        authenticate(MEMBER_USER_ID);
        when(userService.findPlatformRoleNames(MEMBER_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, OTHER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, OTHER_USER_ID));
    }

    private CompanyUser activeMembership(Long userId) {
        CompanyUser membership = new CompanyUser();
        membership.setId(new CompanyUserId(COMPANY_A, userId));
        membership.setMembershipStatus(MembershipStatus.ACTIVE);
        return membership;
    }
}
