package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderStatusRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.CompanyService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAuxiliaryProviderStatusUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private AuxiliaryProviderService auxiliaryProviderService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateAuxiliaryProviderStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateAuxiliaryProviderStatusUseCase(auxiliaryProviderService, companyService,
                currentUserProvider);
        UserPrincipal principal = new UserPrincipal(SECRETARY_USER_ID, "cuenta@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
    }

    private Company company(Long id) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    private AuxiliaryProvider provider(Long id, AuxiliaryProviderStatus status) {
        AuxiliaryProvider provider = new AuxiliaryProvider();
        provider.setProviderId(id);
        provider.setCompanyId(COMPANY_A);
        provider.setProviderKind(AuxiliaryProviderKind.BUSINESS);
        provider.setProviderName("Aux " + id);
        provider.setProviderStatus(status);
        return provider;
    }

    private AuxiliaryProviderStatusRequest request(AuxiliaryProviderStatus status) {
        AuxiliaryProviderStatusRequest request = new AuxiliaryProviderStatusRequest();
        request.setStatus(status);
        return request;
    }

    private void stubProvider(AuxiliaryProviderStatus current) {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(COMPANY_A, 1L))
                .thenReturn(provider(1L, current));
        when(auxiliaryProviderService.save(any(AuxiliaryProvider.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void cambiaDeActiveAInactivo() {
        stubProvider(AuxiliaryProviderStatus.ACTIVE);

        AuxiliaryProviderResponse response = useCase.execute(COMPANY_A, 1L,
                request(AuxiliaryProviderStatus.INACTIVE));

        ArgumentCaptor<AuxiliaryProvider> captor = ArgumentCaptor.forClass(AuxiliaryProvider.class);
        verify(auxiliaryProviderService).save(captor.capture());
        assertEquals(AuxiliaryProviderStatus.INACTIVE, captor.getValue().getProviderStatus());
        assertEquals(1L, captor.getValue().getProviderId());
        assertEquals(AuxiliaryProviderStatus.INACTIVE, response.getProviderStatus());
    }

    @Test
    void cambiaDeInactiveAActive() {
        stubProvider(AuxiliaryProviderStatus.INACTIVE);

        AuxiliaryProviderResponse response = useCase.execute(COMPANY_A, 1L,
                request(AuxiliaryProviderStatus.ACTIVE));

        assertEquals(AuxiliaryProviderStatus.ACTIVE, response.getProviderStatus());
    }

    @Test
    void mismoEstadoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(COMPANY_A, 1L))
                .thenReturn(provider(1L, AuxiliaryProviderStatus.ACTIVE));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, 1L,
                request(AuxiliaryProviderStatus.ACTIVE)));

        verify(auxiliaryProviderService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCambiarEstadoEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, 1L,
                request(AuxiliaryProviderStatus.INACTIVE)));

        verify(auxiliaryProviderService, never()).save(any());
    }
}
