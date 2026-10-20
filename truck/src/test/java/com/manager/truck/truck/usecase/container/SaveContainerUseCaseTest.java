package com.manager.truck.truck.usecase.container;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveContainerUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long CHARGE_A = 300L;

    @Mock
    private ContainerService containerService;
    @Mock
    private ChargeService chargeService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private SaveContainerUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveContainerUseCase(containerService, chargeService, companyService, currentUserProvider);
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

    private Charge charge(Long id, Long companyId, ChargeState state) {
        Charge charge = new Charge();
        charge.setChargeId(id);
        charge.setCompanyId(companyId);
        charge.setContractId(200L);
        charge.setChargeState(state);
        return charge;
    }

    private ContainerRequest request(Long chargeId, String code, String weight) {
        ContainerRequest request = new ContainerRequest();
        request.setChargeId(chargeId);
        request.setContainerCode(code);
        if (weight != null) {
            request.setContainerWeight(new BigDecimal(weight));
        }
        request.setContainerType("DRY");
        return request;
    }

    @Test
    void secretariaCreaContenedorSobreCargaNoCancelada() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, CHARGE_A))
                .thenReturn(charge(CHARGE_A, COMPANY_A, ChargeState.PENDING));
        when(containerService.existsByCompanyIdAndContainerCode(COMPANY_A, "CONT-01")).thenReturn(false);
        when(containerService.save(any(Container.class))).thenAnswer(invocation -> {
            Container container = invocation.getArgument(0);
            container.setContainerId(40L);
            return container;
        });

        ContainerResponse response = useCase.execute(COMPANY_A, request(CHARGE_A, "CONT-01", "1200.500"));

        ArgumentCaptor<Container> captor = ArgumentCaptor.forClass(Container.class);
        verify(containerService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(CHARGE_A, captor.getValue().getChargeId());
        assertEquals("CONT-01", captor.getValue().getContainerCode());
        assertEquals(ContainerState.AVAILABLE, captor.getValue().getContainerState());
        assertEquals(new BigDecimal("1200.500"), captor.getValue().getContainerWeight());
        assertEquals(40L, response.getContainerId());
    }

    @Test
    void creacionDeberiaNormalizarElCodigoConTrim() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, CHARGE_A))
                .thenReturn(charge(CHARGE_A, COMPANY_A, ChargeState.PENDING));
        when(containerService.existsByCompanyIdAndContainerCode(COMPANY_A, "CONT-TRIM")).thenReturn(false);
        when(containerService.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.execute(COMPANY_A, request(CHARGE_A, "  CONT-TRIM  ", null));

        ArgumentCaptor<Container> captor = ArgumentCaptor.forClass(Container.class);
        verify(containerService).save(captor.capture());
        assertEquals("CONT-TRIM", captor.getValue().getContainerCode());
    }

    @Test
    void codigoDuplicadoEnLaEmpresaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, CHARGE_A))
                .thenReturn(charge(CHARGE_A, COMPANY_A, ChargeState.PENDING));
        when(containerService.existsByCompanyIdAndContainerCode(COMPANY_A, "CONT-DUP")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(CHARGE_A, "CONT-DUP", null)));

        verify(containerService, never()).save(any());
    }

    @Test
    void cargaCancelledNoAdmiteContenedores() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, CHARGE_A))
                .thenReturn(charge(CHARGE_A, COMPANY_A, ChargeState.CANCELLED));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(CHARGE_A, "CONT-02", null)));

        verify(containerService, never()).save(any());
    }

    @Test
    void cargaDeliveredSiAdmiteContenedores() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, CHARGE_A))
                .thenReturn(charge(CHARGE_A, COMPANY_A, ChargeState.DELIVERED));
        when(containerService.existsByCompanyIdAndContainerCode(COMPANY_A, "CONT-OK")).thenReturn(false);
        when(containerService.save(any(Container.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ContainerResponse response = useCase.execute(COMPANY_A, request(CHARGE_A, "CONT-OK", null));

        assertEquals(ContainerState.AVAILABLE, response.getContainerState());
    }

    @Test
    void cargaDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(chargeService.findByCompanyIdAndChargeId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Carga no encontrada"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request(999L, "CONT-X", null)));

        verify(containerService, never()).save(any());
    }

    @Test
    void empresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(CHARGE_A, "CONT-X", null)));

        verify(containerService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearContenedorEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request(CHARGE_A, "CONT-X", null)));

        verify(containerService, never()).save(any());
    }
}
