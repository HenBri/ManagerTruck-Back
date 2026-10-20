package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.ChargeService;
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
class SaveChargeUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long CONTRACT_A = 200L;

    @Mock
    private ChargeService chargeService;
    @Mock
    private ContractService contractService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private SaveChargeUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveChargeUseCase(chargeService, contractService, companyService, currentUserProvider);
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

    private Contract contract(Long id, Long companyId, ContractState state) {
        Contract contract = new Contract();
        contract.setContractId(id);
        contract.setCompanyId(companyId);
        contract.setClientId(100L);
        contract.setContractAmount(new BigDecimal("500.00"));
        contract.setContractDate(LocalDate.of(2026, 10, 16));
        contract.setContractState(state);
        return contract;
    }

    private ChargeRequest request(Long contractId) {
        ChargeRequest request = new ChargeRequest();
        request.setContractId(contractId);
        request.setChargeDescription("Carga de prueba");
        request.setChargeType("GRANEL");
        return request;
    }

    @Test
    void secretariaCreaCargaSobreContratoActivo() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, CONTRACT_A))
                .thenReturn(contract(CONTRACT_A, COMPANY_A, ContractState.ACTIVE));
        when(chargeService.save(any(Charge.class))).thenAnswer(invocation -> {
            Charge charge = invocation.getArgument(0);
            charge.setChargeId(30L);
            return charge;
        });

        ChargeResponse response = useCase.execute(COMPANY_A, request(CONTRACT_A));

        ArgumentCaptor<Charge> captor = ArgumentCaptor.forClass(Charge.class);
        verify(chargeService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(CONTRACT_A, captor.getValue().getContractId());
        assertEquals(ChargeState.PENDING, captor.getValue().getChargeState());
        assertEquals(30L, response.getChargeId());
    }

    @Test
    void contratoDraftNoAdmiteCargas() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, CONTRACT_A))
                .thenReturn(contract(CONTRACT_A, COMPANY_A, ContractState.DRAFT));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, request(CONTRACT_A)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void contratoCancelledNoAdmiteCargas() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, CONTRACT_A))
                .thenReturn(contract(CONTRACT_A, COMPANY_A, ContractState.CANCELLED));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, request(CONTRACT_A)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void contratoCompletadoNoAdmiteCargasNuevas() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, CONTRACT_A))
                .thenReturn(contract(CONTRACT_A, COMPANY_A, ContractState.COMPLETED));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, request(CONTRACT_A)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void contratoDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(contractService.findByCompanyIdAndContractId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Contrato no encontrado"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, request(999L)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void empresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, request(CONTRACT_A)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearCargaEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, request(CONTRACT_A)));

        verify(chargeService, never()).save(any());
    }
}
