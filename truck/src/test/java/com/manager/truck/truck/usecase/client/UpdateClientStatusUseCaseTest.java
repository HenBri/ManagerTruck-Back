package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.ClientStatusRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateClientStatusUseCaseTest {

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
    private UpdateClientStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateClientStatusUseCase(clientService, companyService, currentUserProvider);
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

    private Client client(Long id, Boolean state) {
        Client client = new Client();
        client.setClientId(id);
        client.setCompanyId(COMPANY_A);
        client.setClientName("Cliente " + id);
        client.setClientState(state);
        return client;
    }

    private ClientStatusRequest request(Boolean state) {
        ClientStatusRequest request = new ClientStatusRequest();
        request.setClientState(state);
        return request;
    }

    @Test
    void desactivarClienteActivoDeberiaFuncionar() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, 1L)).thenReturn(client(1L, true));
        when(clientService.save(any(Client.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ClientResponse response = useCase.execute(COMPANY_A, 1L, request(false));

        ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
        verify(clientService).save(captor.capture());
        assertEquals(Boolean.FALSE, captor.getValue().getClientState());
        assertEquals(Boolean.FALSE, response.getClientState());
    }

    @Test
    void estadoIgualAlActualDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, 1L)).thenReturn(client(1L, true));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, 1L, request(true)));

        verify(clientService, never()).save(any());
    }

    @Test
    void clienteDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, 99L))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, 99L, request(false)));
    }

    @Test
    void secretariaDeEmpresaANoPuedeCambiarEstadoEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, 1L, request(false)));
        verify(clientService, never()).save(any());
    }
}
