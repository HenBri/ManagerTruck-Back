package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProvidersResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllAuxiliaryProvidersUseCaseTest {

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
    private GetAllAuxiliaryProvidersUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllAuxiliaryProvidersUseCase(auxiliaryProviderService, companyService, currentUserProvider);
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

    private AuxiliaryProvider provider(Long id, String name) {
        AuxiliaryProvider provider = new AuxiliaryProvider();
        provider.setProviderId(id);
        provider.setCompanyId(COMPANY_A);
        provider.setProviderKind(AuxiliaryProviderKind.BUSINESS);
        provider.setProviderName(name);
        provider.setProviderStatus(AuxiliaryProviderStatus.ACTIVE);
        return provider;
    }

    @Test
    void secretariaListaTodosSusProveedores() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<AuxiliaryProvider> page = new PageImpl<>(List.of(provider(1L, "Aux A"), provider(2L, "Aux B")),
                PageRequest.of(0, 20), 2);
        when(auxiliaryProviderService.findByCompanyId(eq(COMPANY_A), any())).thenReturn(page);

        AuxiliaryProvidersResponse response = useCase.execute(COMPANY_A, null, null, null,
                PageRequest.of(0, 20));

        assertEquals(2, response.getAuxiliaryProviders().size());
        assertEquals(2, response.getTotalElements());
        verify(auxiliaryProviderService).findByCompanyId(eq(COMPANY_A), any());
    }

    @Test
    void filtrarPorKindYStatus() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<AuxiliaryProvider> page = new PageImpl<>(List.of(provider(1L, "Aux A")),
                PageRequest.of(0, 20), 1);
        when(auxiliaryProviderService.findByCompanyIdAndProviderKindAndProviderStatus(
                COMPANY_A, AuxiliaryProviderKind.BUSINESS, AuxiliaryProviderStatus.ACTIVE, PageRequest.of(0, 20)))
                .thenReturn(page);

        AuxiliaryProvidersResponse response = useCase.execute(COMPANY_A, AuxiliaryProviderKind.BUSINESS,
                AuxiliaryProviderStatus.ACTIVE, null, PageRequest.of(0, 20));

        assertEquals(1, response.getAuxiliaryProviders().size());
        verify(auxiliaryProviderService).findByCompanyIdAndProviderKindAndProviderStatus(
                COMPANY_A, AuxiliaryProviderKind.BUSINESS, AuxiliaryProviderStatus.ACTIVE, PageRequest.of(0, 20));
    }

    @Test
    void filtrarPorNombreParcial() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<AuxiliaryProvider> page = new PageImpl<>(List.of(provider(1L, "Grúa Norte")),
                PageRequest.of(0, 20), 1);
        when(auxiliaryProviderService.findByCompanyIdAndProviderNameContains(eq(COMPANY_A), eq("Grúa"),
                any())).thenReturn(page);

        AuxiliaryProvidersResponse response = useCase.execute(COMPANY_A, null, null, "  Grúa  ",
                PageRequest.of(0, 20));

        assertEquals(1, response.getAuxiliaryProviders().size());
        verify(auxiliaryProviderService).findByCompanyIdAndProviderNameContains(eq(COMPANY_A), eq("Grúa"),
                any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeListarEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, null, null, null, PageRequest.of(0, 20)));

        verify(auxiliaryProviderService, never()).findByCompanyId(any(), any());
    }
}
