package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.response.ClientsResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
class GetAllClientsUseCaseTest {

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
    private GetAllClientsUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllClientsUseCase(clientService, companyService, currentUserProvider);
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

    private Client client(Long id, Long companyId, Boolean state) {
        Client client = new Client();
        client.setClientId(id);
        client.setCompanyId(companyId);
        client.setClientName("Cliente " + id);
        client.setClientState(state);
        return client;
    }

    @Test
    void listadoSinFiltroDeberiaUsarBusquedaPorEmpresa() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Client> page = new PageImpl<>(List.of(client(1L, COMPANY_A, true)), PageRequest.of(0, 20), 1);
        when(clientService.findByCompanyId(COMPANY_A, PageRequest.of(0, 20))).thenReturn(page);

        ClientsResponse response = useCase.execute(COMPANY_A, null, PageRequest.of(0, 20));

        assertEquals(1, response.getClients().size());
        assertEquals(1, response.getTotalElements());
        verify(clientService, never()).findByCompanyIdAndClientState(any(), any(), any());
    }

    @Test
    void listadoConFiltroActivoDeberiaFiltrarPorEstado() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Client> page = new PageImpl<>(List.of(client(1L, COMPANY_A, true)), PageRequest.of(0, 20), 1);
        when(clientService.findByCompanyIdAndClientState(COMPANY_A, true, PageRequest.of(0, 20)))
                .thenReturn(page);

        ClientsResponse response = useCase.execute(COMPANY_A, true, PageRequest.of(0, 20));

        assertEquals(1, response.getClients().size());
        verify(clientService).findByCompanyIdAndClientState(COMPANY_A, true, PageRequest.of(0, 20));
    }

    @Test
    void secretariaDeEmpresaANoPuedeListarClientesDeLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, null, Pageable.unpaged()));

        verify(clientService, never()).findByCompanyId(any(), any());
    }

    @Test
    void listadoDeberiaMapearPaginacion() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Client> page = new PageImpl<>(
                List.of(client(6L, COMPANY_A, true), client(7L, COMPANY_A, true),
                        client(8L, COMPANY_A, true), client(9L, COMPANY_A, true),
                        client(10L, COMPANY_A, true)),
                PageRequest.of(1, 5), 12);
        when(clientService.findByCompanyId(COMPANY_A, PageRequest.of(1, 5))).thenReturn(page);

        ClientsResponse response = useCase.execute(COMPANY_A, null, PageRequest.of(1, 5));

        assertEquals(1, response.getPage());
        assertEquals(5, response.getSize());
        assertEquals(5, response.getClients().size());
        assertEquals(12, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
    }
}
