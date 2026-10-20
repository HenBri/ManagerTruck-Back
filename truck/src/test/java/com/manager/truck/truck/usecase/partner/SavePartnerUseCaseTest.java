package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.PartnerService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavePartnerUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long LINKED_USER_ID = 30L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private PartnerService partnerService;
    @Mock
    private CompanyService companyService;
    @Mock
    private CompanyUserService companyUserService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private SavePartnerUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SavePartnerUseCase(partnerService, companyService, companyUserService,
                userService, currentUserProvider);
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

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
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

    private User user(Long userId, UserStatus status) {
        User user = new User();
        user.setUserId(userId);
        user.setUserEmail("cuenta@mail.com");
        user.setUserName("Cuenta");
        user.setUserLastname("Prueba");
        user.setUserStatus(status);
        return user;
    }

    private PartnerRequest request(Long userId) {
        PartnerRequest request = new PartnerRequest();
        request.setPartnerType(PartnerType.PERSON);
        request.setPartnerName("Socio Nuevo");
        request.setUserId(userId);
        return request;
    }

    private void stubActiveMembership() {
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, LINKED_USER_ID, MembershipStatus.ACTIVE)));
        when(userService.findById(LINKED_USER_ID)).thenReturn(user(LINKED_USER_ID, UserStatus.ACTIVE));
    }

    @Test
    void secretariaCreaSocioConCuentaVinculadaEnSuEmpresa() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        stubActiveMembership();
        when(partnerService.existsByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID)).thenReturn(false);
        when(partnerService.save(any(Partner.class))).thenAnswer(invocation -> {
            Partner partner = invocation.getArgument(0);
            partner.setPartnerId(77L);
            return partner;
        });

        PartnerResponse response = useCase.execute(COMPANY_A, request(LINKED_USER_ID));

        ArgumentCaptor<Partner> captor = ArgumentCaptor.forClass(Partner.class);
        verify(partnerService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(LINKED_USER_ID, captor.getValue().getUserId());
        assertEquals(PartnerStatus.ACTIVE, captor.getValue().getPartnerState());
        assertEquals(77L, response.getPartnerId());
    }

    @Test
    void secretariaCreaSocioSinCuentaDeAcceso() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(partnerService.save(any(Partner.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PartnerResponse response = useCase.execute(COMPANY_A, request(null));

        assertEquals(null, response.getUserId());
        verify(companyUserService, never()).findByCompanyIdAndUserId(any(), any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearSocioEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request(null)));

        verify(partnerService, never()).save(any());
    }

    @Test
    void cuentaYaVinculadaComoSocioDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        stubActiveMembership();
        when(partnerService.existsByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID)).thenReturn(true);

        ConflictException exception = assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(LINKED_USER_ID)));

        assertEquals(true, exception.getMessage().contains("duplicadas"));
        verify(partnerService, never()).save(any());
    }

    @Test
    void cuentaSinMembresiaEnLaEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request(LINKED_USER_ID)));

        verify(partnerService, never()).save(any());
    }

    @Test
    void membresiaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, LINKED_USER_ID, MembershipStatus.INACTIVE)));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(LINKED_USER_ID)));

        verify(partnerService, never()).save(any());
    }

    @Test
    void cuentaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(companyUserService.findByCompanyIdAndUserId(COMPANY_A, LINKED_USER_ID))
                .thenReturn(Optional.of(membership(COMPANY_A, LINKED_USER_ID, MembershipStatus.ACTIVE)));
        when(userService.findById(LINKED_USER_ID)).thenReturn(user(LINKED_USER_ID, UserStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(LINKED_USER_ID)));

        verify(partnerService, never()).save(any());
    }

    @Test
    void socioEnEmpresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(null)));

        verify(partnerService, never()).save(any());
    }
}
