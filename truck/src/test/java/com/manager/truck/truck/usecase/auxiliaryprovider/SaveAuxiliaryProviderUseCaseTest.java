package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderRequest;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveAuxiliaryProviderUseCaseTest {

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
    private SaveAuxiliaryProviderUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveAuxiliaryProviderUseCase(auxiliaryProviderService, companyService, currentUserProvider);
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

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyStatus(status);
        return company;
    }

    private AuxiliaryProviderRequest request(AuxiliaryProviderKind kind, String name, String document,
                                             String phone, String email) {
        AuxiliaryProviderRequest request = new AuxiliaryProviderRequest();
        request.setProviderKind(kind);
        request.setProviderName(name);
        request.setProviderDocument(document);
        request.setProviderPhone(phone);
        request.setProviderEmail(email);
        return request;
    }

    @Test
    void secretariaCreaProveedorDeAuxilio() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(auxiliaryProviderService.existsByCompanyIdAndProviderName(COMPANY_A, "Grúa Urbana")).thenReturn(false);
        when(auxiliaryProviderService.save(any(AuxiliaryProvider.class))).thenAnswer(invocation -> {
            AuxiliaryProvider provider = invocation.getArgument(0);
            provider.setProviderId(40L);
            return provider;
        });

        AuxiliaryProviderResponse response = useCase.execute(COMPANY_A,
                request(AuxiliaryProviderKind.INDEPENDENT, "  Grúa Urbana  ", " DOC-1 ", "  ", null));

        ArgumentCaptor<AuxiliaryProvider> captor = ArgumentCaptor.forClass(AuxiliaryProvider.class);
        verify(auxiliaryProviderService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(AuxiliaryProviderKind.INDEPENDENT, captor.getValue().getProviderKind());
        assertEquals("Grúa Urbana", captor.getValue().getProviderName());
        assertEquals(AuxiliaryProviderStatus.ACTIVE, captor.getValue().getProviderStatus());
        assertEquals(40L, response.getProviderId());
        assertEquals(COMPANY_A, response.getCompanyId());
        assertEquals("Grúa Urbana", response.getProviderName());
        assertEquals(AuxiliaryProviderStatus.ACTIVE, response.getProviderStatus());
    }

    @Test
    void nombreDuplicadoEnLaEmpresaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(auxiliaryProviderService.existsByCompanyIdAndProviderName(COMPANY_A, "Aux-Dup")).thenReturn(true);

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A,
                request(AuxiliaryProviderKind.BUSINESS, "Aux-Dup", null, null, null)));

        verify(auxiliaryProviderService, never()).save(any());
    }

    @Test
    void empresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A,
                request(AuxiliaryProviderKind.BUSINESS, "Aux", null, null, null)));

        verify(auxiliaryProviderService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B,
                request(AuxiliaryProviderKind.BUSINESS, "Aux", null, null, null)));

        verify(auxiliaryProviderService, never()).save(any());
    }

    @Test
    void creacionDeberiaNormalizarElNombreConTrim() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(auxiliaryProviderService.existsByCompanyIdAndProviderName(COMPANY_A, "Aux Trim")).thenReturn(false);
        when(auxiliaryProviderService.save(any(AuxiliaryProvider.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_A, request(AuxiliaryProviderKind.BUSINESS, "  Aux Trim  ", "   ", " 555 ", null));

        ArgumentCaptor<AuxiliaryProvider> captor = ArgumentCaptor.forClass(AuxiliaryProvider.class);
        verify(auxiliaryProviderService).save(captor.capture());
        assertEquals("Aux Trim", captor.getValue().getProviderName());
        assertNull(captor.getValue().getProviderDocument());
        assertEquals("555", captor.getValue().getProviderPhone());
        assertNull(captor.getValue().getProviderEmail());
    }
}
