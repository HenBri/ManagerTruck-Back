package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderUpdateRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateAuxiliaryProviderUseCaseTest {

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
    private UpdateAuxiliaryProviderUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateAuxiliaryProviderUseCase(auxiliaryProviderService, companyService, currentUserProvider);
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

    private AuxiliaryProviderUpdateRequest request(AuxiliaryProviderKind kind, String name, String document,
                                                   String phone, String email) {
        AuxiliaryProviderUpdateRequest request = new AuxiliaryProviderUpdateRequest();
        request.setProviderKind(kind);
        request.setProviderName(name);
        request.setProviderDocument(document);
        request.setProviderPhone(phone);
        request.setProviderEmail(email);
        return request;
    }

    @Test
    void actualizaEscalaresDelProveedor() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(COMPANY_A, 1L))
                .thenReturn(provider(1L, "Viejo"));
        when(auxiliaryProviderService.existsByCompanyIdAndProviderNameNot(COMPANY_A, "Nuevo", 1L))
                .thenReturn(false);
        when(auxiliaryProviderService.save(any(AuxiliaryProvider.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AuxiliaryProviderResponse response = useCase.execute(COMPANY_A, 1L,
                request(AuxiliaryProviderKind.INDEPENDENT, "  Nuevo  ", " 12345 ", "  ", "  a@b.com  "));

        ArgumentCaptor<AuxiliaryProvider> captor = ArgumentCaptor.forClass(AuxiliaryProvider.class);
        verify(auxiliaryProviderService).save(captor.capture());
        assertEquals(AuxiliaryProviderKind.INDEPENDENT, captor.getValue().getProviderKind());
        assertEquals("Nuevo", captor.getValue().getProviderName());
        assertEquals("12345", captor.getValue().getProviderDocument());
        assertNull(captor.getValue().getProviderPhone());
        assertEquals("a@b.com", captor.getValue().getProviderEmail());
        assertEquals(AuxiliaryProviderStatus.ACTIVE, captor.getValue().getProviderStatus());
        assertEquals("Nuevo", response.getProviderName());
    }

    @Test
    void nombreDuplicadoDeOtraFilaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(COMPANY_A, 1L))
                .thenReturn(provider(1L, "Viejo"));
        when(auxiliaryProviderService.existsByCompanyIdAndProviderNameNot(COMPANY_A, "Duplicado", 1L))
                .thenReturn(true);

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, 1L,
                request(AuxiliaryProviderKind.BUSINESS, "  Duplicado  ", null, null, null)));

        verify(auxiliaryProviderService, never()).save(any());
    }

    @Test
    void proveedorNoEncontradoDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(COMPANY_A, 99L))
                .thenThrow(new ResourceNotFoundException("Proveedor de auxilio no encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, 99L,
                request(AuxiliaryProviderKind.BUSINESS, "Aux", null, null, null)));

        verify(auxiliaryProviderService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeActualizarEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, 1L,
                request(AuxiliaryProviderKind.BUSINESS, "Aux", null, null, null)));

        verify(auxiliaryProviderService, never()).save(any());
        verify(auxiliaryProviderService, never()).existsByCompanyIdAndProviderNameNot(anyLong(), any(), anyLong());
    }
}
