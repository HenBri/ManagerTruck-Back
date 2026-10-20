package com.manager.truck.truck.usecase.companyuser;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.CompanyMembershipStatusRequest;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateCompanyMembershipStatusUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long MEMBER_USER_ID = 5L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private CompanyUserService companyUserService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateCompanyMembershipStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateCompanyMembershipStatusUseCase(companyUserService, companyService, userService,
                currentUserProvider);
        UserPrincipal principal = new UserPrincipal(SECRETARY_USER_ID, "secretaria@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private CompanyUser membership(Long companyId, Long userId, MembershipStatus status) {
        CompanyUser membership = new CompanyUser();
        membership.setId(new CompanyUserId(companyId, userId));
        membership.setMembershipStatus(status);
        return membership;
    }

    private User member() {
        User user = new User();
        user.setUserId(MEMBER_USER_ID);
        user.setUserEmail("miembro@mail.com");
        user.setUserName("Miembro");
        user.setUserLastname("Prueba");
        user.setUserStatus(UserStatus.ACTIVE);
        return user;
    }

    private CompanyMembershipStatusRequest request(MembershipStatus status) {
        CompanyMembershipStatusRequest request = new CompanyMembershipStatusRequest();
        request.setStatus(status);
        return request;
    }

    @Test
    void secretariaDeLaEmpresaPuedeDesactivarUnaMembresia() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.ACTIVE)));
        when(userService.findById(MEMBER_USER_ID)).thenReturn(member());
        when(companyUserService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyMembershipResponse response = useCase.execute(COMPANY_A, MEMBER_USER_ID,
                request(MembershipStatus.INACTIVE));

        assertEquals(MembershipStatus.INACTIVE, response.getMembershipStatus());
        verify(companyUserService).save(any());
    }

    @Test
    void estadoRepetidoDeberiaDevolver409() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.INACTIVE)));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, MEMBER_USER_ID,
                request(MembershipStatus.INACTIVE)));

        verify(companyUserService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCambiarEstadoEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, MEMBER_USER_ID,
                request(MembershipStatus.INACTIVE)));

        verify(companyUserService, never()).save(any());
    }

    @Test
    void membresiaInexistenteDeberiaDevolver404() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, MEMBER_USER_ID,
                request(MembershipStatus.INACTIVE)));
    }

    @Test
    void reactivarUnaMembresiaDeberiaSerPosible() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of("SECRETARY"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, MEMBER_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, MEMBER_USER_ID, MembershipStatus.INACTIVE)));
        when(userService.findById(MEMBER_USER_ID)).thenReturn(member());
        when(userService.findCompanyRoleNames(MEMBER_USER_ID, COMPANY_A)).thenReturn(List.of("PARTNER"));
        when(companyUserService.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyMembershipResponse response = useCase.execute(COMPANY_A, MEMBER_USER_ID,
                request(MembershipStatus.ACTIVE));

        assertEquals(MembershipStatus.ACTIVE, response.getMembershipStatus());
    }
}
