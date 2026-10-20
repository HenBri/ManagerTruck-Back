package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.ChargeStatusRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.ChargeService;
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
class UpdateChargeStatusUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;

    @Mock
    private ChargeService chargeService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateChargeStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateChargeStatusUseCase(chargeService, companyService, currentUserProvider);
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

    private Charge charge(Long id, ChargeState state) {
        Charge charge = new Charge();
        charge.setChargeId(id);
        charge.setCompanyId(COMPANY_A);
        charge.setContractId(200L);
        charge.setChargeState(state);
        return charge;
    }

    private ChargeStatusRequest request(ChargeState state) {
        ChargeStatusRequest request = new ChargeStatusRequest();
        request.setStatus(state);
        return request;
    }

    private void stubCharge(ChargeState current) {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 1L)).thenReturn(charge(1L, current));
        when(chargeService.save(any(Charge.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void pendingPuedeIniciarTransito() {
        stubCharge(ChargeState.PENDING);

        ChargeResponse response = useCase.execute(COMPANY_A, 1L, request(ChargeState.IN_TRANSIT));

        ArgumentCaptor<Charge> captor = ArgumentCaptor.forClass(Charge.class);
        verify(chargeService).save(captor.capture());
        assertEquals(ChargeState.IN_TRANSIT, captor.getValue().getChargeState());
        assertEquals(ChargeState.IN_TRANSIT, response.getChargeState());
    }

    @Test
    void pendingPuedeCancelarse() {
        stubCharge(ChargeState.PENDING);

        ChargeResponse response = useCase.execute(COMPANY_A, 1L, request(ChargeState.CANCELLED));

        assertEquals(ChargeState.CANCELLED, response.getChargeState());
    }

    @Test
    void inTransitPuedeEntregarse() {
        stubCharge(ChargeState.IN_TRANSIT);

        ChargeResponse response = useCase.execute(COMPANY_A, 1L, request(ChargeState.DELIVERED));

        assertEquals(ChargeState.DELIVERED, response.getChargeState());
    }

    @Test
    void inTransitPuedeCancelarse() {
        stubCharge(ChargeState.IN_TRANSIT);

        ChargeResponse response = useCase.execute(COMPANY_A, 1L, request(ChargeState.CANCELLED));

        assertEquals(ChargeState.CANCELLED, response.getChargeState());
    }

    @Test
    void pendingNoPuedeEntregarseDirectamente() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 1L))
                .thenReturn(charge(1L, ChargeState.PENDING));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ChargeState.DELIVERED)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void deliveredEsEstadoTerminal() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 1L))
                .thenReturn(charge(1L, ChargeState.DELIVERED));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ChargeState.IN_TRANSIT)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void cancelledEsEstadoTerminal() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 1L))
                .thenReturn(charge(1L, ChargeState.CANCELLED));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ChargeState.PENDING)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void mismoEstadoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 1L))
                .thenReturn(charge(1L, ChargeState.PENDING));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, 1L, request(ChargeState.PENDING)));

        verify(chargeService, never()).save(any());
    }

    @Test
    void cargaDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 99L))
                .thenThrow(new ResourceNotFoundException("Carga no encontrada"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, 99L, request(ChargeState.IN_TRANSIT)));
    }

    @Test
    void secretariaDeEmpresaANoPuedeCambiarEstadoEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, 1L, request(ChargeState.IN_TRANSIT)));

        verify(chargeService, never()).save(any());
    }
}
