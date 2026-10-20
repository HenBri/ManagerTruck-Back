package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.ClientService;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveClientUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private ClientService clientService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private SaveClientUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveClientUseCase(clientService, companyService, currentUserProvider);
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

    private ClientRequest request(String name) {
        ClientRequest request = new ClientRequest();
        request.setClientName(name);
        request.setClientCi("123456");
        request.setClientEmail("cliente@mail.com");
        return request;
    }

    @Test
    void secretariaCreaClienteEnSuEmpresa() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.save(any(Client.class))).thenAnswer(invocation -> {
            Client client = invocation.getArgument(0);
            client.setClientId(55L);
            return client;
        });

        ClientResponse response = useCase.execute(COMPANY_A, request("Cliente Nuevo"));

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals("Cliente Nuevo", captor.getValue().getClientName());
        assertEquals(Boolean.TRUE, captor.getValue().getClientState());
        assertEquals(55L, response.getClientId());
    }

    @Test
    void creacionDeberiaNormalizarElNombreConTrim() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_A, request("  Cliente con espacios  "));

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientService).save(captor.capture());
        assertEquals("Cliente con espacios", captor.getValue().getClientName());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearClienteEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request("Cliente")));

        verify(clientService, never()).save(any());
    }

    @Test
    void clienteEnEmpresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request("Cliente")));

        verify(clientService, never()).save(any());
    }

    @Test
    void superAdminPuedeCrearClienteEnTenantSeleccionado() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of("SUPER_ADMIN"));
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse response = useCase.execute(COMPANY_A, request("Cliente Super"));

        assertTrue(response.getClientId() == null || response.getClientId() >= 0);
        verify(clientService).save(any(Client.class));
    }
}
