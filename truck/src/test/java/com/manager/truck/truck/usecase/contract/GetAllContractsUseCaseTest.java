package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllContractsUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long CLIENT_A = 100L;

    @Mock
    private ContractService contractService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private GetAllContractsUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllContractsUseCase(contractService, companyService, currentUserProvider);
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

    private Contract contract(Long id) {
        Contract contract = new Contract();
        contract.setContractId(id);
        contract.setCompanyId(COMPANY_A);
        contract.setClientId(CLIENT_A);
        contract.setContractAmount(new BigDecimal("100.00"));
        contract.setContractDate(LocalDate.of(2026, 10, 16));
        contract.setContractState(ContractState.DRAFT);
        return contract;
    }

    @Test
    void listadoSinFiltrosDeberiaBuscarPorEmpresa() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Contract> page = new PageImpl<>(List.of(contract(1L)), PageRequest.of(0, 20), 1);
        when(contractService.findByCompanyId(eq(COMPANY_A), any())).thenReturn(page);

        ContractsResponse response = useCase.execute(COMPANY_A, null, null, PageRequest.of(0, 20));

        assertEquals(1, response.getContracts().size());
    }

    @Test
    void listadoConFiltroDeCliente() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Contract> page = new PageImpl<>(List.of(contract(1L)), PageRequest.of(0, 20), 1);
        when(contractService.findByCompanyIdAndClientId(COMPANY_A, CLIENT_A, PageRequest.of(0, 20)))
                .thenReturn(page);

        ContractsResponse response = useCase.execute(COMPANY_A, CLIENT_A, null, PageRequest.of(0, 20));

        assertEquals(1, response.getContracts().size());
        verify(contractService).findByCompanyIdAndClientId(COMPANY_A, CLIENT_A, PageRequest.of(0, 20));
    }

    @Test
    void listadoConFiltroDeEstado() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Contract> page = new PageImpl<>(List.of(contract(1L)), PageRequest.of(0, 20), 1);
        when(contractService.findByCompanyIdAndContractState(COMPANY_A, ContractState.ACTIVE, PageRequest.of(0, 20)))
                .thenReturn(page);

        ContractsResponse response = useCase.execute(COMPANY_A, null, ContractState.ACTIVE, PageRequest.of(0, 20));

        assertEquals(1, response.getContracts().size());
    }

    @Test
    void listadoConClienteYEstadoCombinados() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Page<Contract> page = new PageImpl<>(List.of(contract(1L)), PageRequest.of(0, 20), 1);
        when(contractService.findByCompanyIdAndClientIdAndContractState(
                COMPANY_A, CLIENT_A, ContractState.DRAFT, PageRequest.of(0, 20))).thenReturn(page);

        ContractsResponse response = useCase.execute(COMPANY_A, CLIENT_A, ContractState.DRAFT,
                PageRequest.of(0, 20));

        assertEquals(1, response.getContracts().size());
    }

    @Test
    void secretariaDeEmpresaANoPuedeListarContratosDeLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, null, null, PageRequest.of(0, 20)));

        verify(contractService, never()).findByCompanyId(any(), any());
    }
}
