package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveContractUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long CLIENT_A = 100L;

    @Mock
    private ContractService contractService;
    @Mock
    private ClientService clientService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private SaveContractUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveContractUseCase(contractService, clientService, companyService, currentUserProvider);
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

    private Client client(Long id, Long companyId, Boolean state) {
        Client client = new Client();
        client.setClientId(id);
        client.setCompanyId(companyId);
        client.setClientName("Cliente " + id);
        client.setClientState(state);
        return client;
    }

    private ContractRequest request(Long clientId, String amount) {
        ContractRequest request = new ContractRequest();
        request.setClientId(clientId);
        request.setContractAmount(new BigDecimal(amount));
        request.setContractDate(LocalDate.of(2026, 10, 16));
        return request;
    }

    @Test
    void secretariaCreaContratoParaClienteActivoDeSuEmpresa() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, CLIENT_A))
                .thenReturn(client(CLIENT_A, COMPANY_A, true));
        when(contractService.save(any(Contract.class))).thenAnswer(invocation -> {
            Contract contract = invocation.getArgument(0);
            contract.setContractId(10L);
            return contract;
        });

        ContractResponse response = useCase.execute(COMPANY_A, request(CLIENT_A, "1500.50"));

        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(CLIENT_A, captor.getValue().getClientId());
        assertEquals(new BigDecimal("1500.50"), captor.getValue().getContractAmount());
        assertEquals(ContractState.DRAFT, captor.getValue().getContractState());
        assertEquals(10L, response.getContractId());
    }

    @Test
    void clienteDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Cliente no encontrado"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request(999L, "100.00")));

        verify(contractService, never()).save(any());
    }

    @Test
    void clienteInactivoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, CLIENT_A))
                .thenReturn(client(CLIENT_A, COMPANY_A, false));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(CLIENT_A, "100.00")));

        verify(contractService, never()).save(any());
    }

    @Test
    void empresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(CLIENT_A, "100.00")));

        verify(contractService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearContratoEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request(CLIENT_A, "100.00")));

        verify(contractService, never()).save(any());
    }

    @Test
    void montoCeroDeberiaSerAceptado() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(clientService.findByCompanyIdAndClientId(COMPANY_A, CLIENT_A))
                .thenReturn(client(CLIENT_A, COMPANY_A, true));
        when(contractService.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContractResponse response = useCase.execute(COMPANY_A, request(CLIENT_A, "0.00"));

        assertEquals(0, response.getContractAmount().compareTo(BigDecimal.ZERO));
    }
}
