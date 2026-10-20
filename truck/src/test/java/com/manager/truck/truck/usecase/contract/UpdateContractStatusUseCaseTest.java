package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
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
class UpdateContractStatusUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private ContractService contractService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateContractStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateContractStatusUseCase(contractService, companyService, currentUserProvider);
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

    private Contract contract(Long id, ContractState state) {
        Contract contract = new Contract();
        contract.setContractId(id);
        contract.setCompanyId(COMPANY_A);
        contract.setClientId(100L);
        contract.setContractAmount(new BigDecimal("100.00"));
        contract.setContractDate(LocalDate.of(2026, 10, 16));
        contract.setContractState(state);
        return contract;
    }

    private ContractStatusRequest request(ContractState state) {
        ContractStatusRequest request = new ContractStatusRequest();
        request.setStatus(state);
        return request;
    }

    private void stubTransition(ContractState current, ContractState target) {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 1L)).thenReturn(contract(1L, current));
        when(contractService.save(any(Contract.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void draftPuedeActivarse() {
        stubTransition(ContractState.DRAFT, ContractState.ACTIVE);

        ContractResponse response = useCase.execute(COMPANY_A, 1L, request(ContractState.ACTIVE));

        ArgumentCaptor<Contract> captor = ArgumentCaptor.forClass(Contract.class);
        verify(contractService).save(captor.capture());
        assertEquals(ContractState.ACTIVE, captor.getValue().getContractState());
        assertEquals(ContractState.ACTIVE, response.getContractState());
    }

    @Test
    void draftPuedeCancelarse() {
        stubTransition(ContractState.DRAFT, ContractState.CANCELLED);

        ContractResponse response = useCase.execute(COMPANY_A, 1L, request(ContractState.CANCELLED));

        assertEquals(ContractState.CANCELLED, response.getContractState());
    }

    @Test
    void activePuedeCompletarse() {
        stubTransition(ContractState.ACTIVE, ContractState.COMPLETED);

        ContractResponse response = useCase.execute(COMPANY_A, 1L, request(ContractState.COMPLETED));

        assertEquals(ContractState.COMPLETED, response.getContractState());
    }

    @Test
    void activePuedeCancelarse() {
        stubTransition(ContractState.ACTIVE, ContractState.CANCELLED);

        ContractResponse response = useCase.execute(COMPANY_A, 1L, request(ContractState.CANCELLED));

        assertEquals(ContractState.CANCELLED, response.getContractState());
    }

    @Test
    void draftNoPuedeCompletarseDirectamente() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 1L))
                .thenReturn(contract(1L, ContractState.DRAFT));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContractState.COMPLETED)));

        verify(contractService, never()).save(any());
    }

    @Test
    void completedEsEstadoTerminal() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 1L))
                .thenReturn(contract(1L, ContractState.COMPLETED));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContractState.ACTIVE)));

        verify(contractService, never()).save(any());
    }

    @Test
    void cancelledEsEstadoTerminal() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 1L))
                .thenReturn(contract(1L, ContractState.CANCELLED));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContractState.ACTIVE)));

        verify(contractService, never()).save(any());
    }

    @Test
    void mismoEstadoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 1L))
                .thenReturn(contract(1L, ContractState.ACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ContractState.ACTIVE)));

        verify(contractService, never()).save(any());
    }

    @Test
    void contratoDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 99L))
                .thenThrow(new com.manager.truck.truck.exception.ResourceNotFoundException("Contrato no encontrado"));

        assertThrows(com.manager.truck.truck.exception.ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, 99L, request(ContractState.ACTIVE)));
    }

    @Test
    void secretariaDeEmpresaANoPuedeCambiarEstadoEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, 1L, request(ContractState.ACTIVE)));

        verify(contractService, never()).save(any());
    }
}
