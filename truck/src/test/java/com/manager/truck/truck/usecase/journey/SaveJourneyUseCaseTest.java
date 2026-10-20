package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.domain.enums.AuxiliaryReason;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContainerService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.TruckService;
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
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaveJourneyUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long CONTAINER_ID = 100L;
    private static final Long CHARGE_ID = 300L;
    private static final Long ASSIGNMENT_ID = 400L;
    private static final Long TRUCK_ID = 55L;
    private static final Long DRIVER_ID = 77L;
    private static final Long PROVIDER_ID = 66L;
    private static final Long SAVED_JOURNEY_ID = 900L;

    @Mock
    private JourneyService journeyService;
    @Mock
    private ContainerService containerService;
    @Mock
    private ChargeService chargeService;
    @Mock
    private TruckDriverAssignmentService assignmentService;
    @Mock
    private TruckService truckService;
    @Mock
    private DriverService driverService;
    @Mock
    private AuxiliaryProviderService auxiliaryProviderService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private SaveJourneyUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new SaveJourneyUseCase(journeyService, containerService, chargeService, assignmentService,
                truckService, driverService, auxiliaryProviderService, companyService, currentUserProvider);
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
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private Container container(Long id, Long chargeId, ContainerState state) {
        Container container = new Container();
        container.setContainerId(id);
        container.setCompanyId(COMPANY_A);
        container.setChargeId(chargeId);
        container.setContainerCode("CONT-01");
        container.setContainerState(state);
        return container;
    }

    private Charge charge(Long id, ChargeState state) {
        Charge charge = new Charge();
        charge.setChargeId(id);
        charge.setCompanyId(COMPANY_A);
        charge.setContractId(200L);
        charge.setChargeState(state);
        return charge;
    }

    private TruckDriverAssignment assignment(Long id, Long truckId, Long driverId,
                                             LocalDateTime from, LocalDateTime until) {
        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setAssignmentId(id);
        assignment.setCompanyId(COMPANY_A);
        assignment.setTruckId(truckId);
        assignment.setDriverId(driverId);
        assignment.setAssignedFrom(from != null ? from : LocalDateTime.now().minusDays(30));
        assignment.setAssignedUntil(until);
        return assignment;
    }

    private Truck truck(Long id, TruckStatus state) {
        Truck truck = new Truck();
        truck.setTruckId(id);
        truck.setCompanyId(COMPANY_A);
        truck.setTruckState(state);
        return truck;
    }

    private Driver driver(Long id, DriverStatus state) {
        Driver driver = new Driver();
        driver.setDriverId(id);
        driver.setCompanyId(COMPANY_A);
        driver.setDriverState(state);
        return driver;
    }

    private AuxiliaryProvider provider(Long id, AuxiliaryProviderStatus status) {
        AuxiliaryProvider provider = new AuxiliaryProvider();
        provider.setProviderId(id);
        provider.setCompanyId(COMPANY_A);
        provider.setProviderName("Grua " + id);
        provider.setProviderStatus(status);
        return provider;
    }

    private JourneyRequest requestBase(Long containerId) {
        JourneyRequest request = new JourneyRequest();
        request.setContainerId(containerId);
        request.setJourneyOrigin("  Origen  ");
        request.setJourneyDestination(" Destino ");
        request.setJourneyDescription("  Descripción  ");
        request.setJourneyType(" FLETE ");
        return request;
    }

    private JourneyRequest requestPartner(Long containerId, Long assignmentId) {
        JourneyRequest request = requestBase(containerId);
        request.setExecutionType(JourneyExecutionType.PARTNER);
        request.setAssignmentId(assignmentId);
        return request;
    }

    private JourneyRequest requestAuxiliary(Long containerId, Long providerId, String plate,
                                            String driverName, AuxiliaryReason reason, BigDecimal amount) {
        JourneyRequest request = requestBase(containerId);
        request.setExecutionType(JourneyExecutionType.AUXILIARY);
        request.setAuxiliaryProviderId(providerId);
        request.setAuxiliaryTruckPlate(plate);
        request.setAuxiliaryDriverName(driverName);
        request.setAuxiliaryReason(reason);
        request.setAgreedAmount(amount);
        return request;
    }

    private void stubCompany(Long companyId, CompanyStatus status) {
        when(companyService.findById(companyId)).thenReturn(company(companyId, status));
    }

    private void stubContainerAndCharge(Long companyId, ContainerState containerState, ChargeState chargeState) {
        when(containerService.findByCompanyIdAndContainerId(companyId, CONTAINER_ID))
                .thenReturn(container(CONTAINER_ID, CHARGE_ID, containerState));
        when(chargeService.findByCompanyIdAndChargeId(companyId, CHARGE_ID))
                .thenReturn(charge(CHARGE_ID, chargeState));
    }

    private void stubSaveBase(Long companyId) {
        stubSecretaryOf(companyId);
        stubCompany(companyId, CompanyStatus.ACTIVE);
        stubContainerAndCharge(companyId, ContainerState.LOADED, ChargeState.PENDING);
    }

    private void stubPartnerFlow(Long companyId) {
        when(assignmentService.findByCompanyIdAndAssignmentId(companyId, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, TRUCK_ID, DRIVER_ID, null, null));
        when(truckService.findByCompanyIdAndTruckId(companyId, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, TruckStatus.ACTIVE));
        when(driverService.findByCompanyIdAndDriverId(companyId, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, DriverStatus.ACTIVE));
        when(journeyService.existsActiveByCompanyIdAndAssignmentId(companyId, ASSIGNMENT_ID,
                SaveJourneyUseCase.ACTIVE_STATES)).thenReturn(false);
    }

    private void stubAuxiliaryProvider(Long companyId, AuxiliaryProviderStatus status) {
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(companyId, PROVIDER_ID))
                .thenReturn(provider(PROVIDER_ID, status));
    }

    private void stubSave() {
        when(journeyService.save(any(Journey.class))).thenAnswer(invocation -> {
            Journey journey = invocation.getArgument(0);
            journey.setJourneyId(SAVED_JOURNEY_ID);
            return journey;
        });
    }

    @Test
    void secretariaCreaViajePartnerValido() {
        stubSaveBase(COMPANY_A);
        stubPartnerFlow(COMPANY_A);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID));

        ArgumentCaptor<Journey> captor = ArgumentCaptor.forClass(Journey.class);
        verify(journeyService).save(captor.capture());
        Journey saved = captor.getValue();
        assertEquals(COMPANY_A, saved.getCompanyId());
        assertEquals(CONTAINER_ID, saved.getContainerId());
        assertEquals(JourneyState.PLANNED, saved.getJourneyState());
        assertEquals(JourneyExecutionType.PARTNER, saved.getExecutionType());
        assertEquals(ASSIGNMENT_ID, saved.getAssignmentId());
        assertNull(saved.getAuxiliaryProviderId());
        assertNull(saved.getAuxiliaryTruckPlate());
        assertNull(saved.getAuxiliaryDriverName());
        assertNull(saved.getAuxiliaryReason());
        assertNull(saved.getAgreedAmount());
        assertEquals("Origen", saved.getJourneyOrigin());
        assertEquals("Destino", saved.getJourneyDestination());
        assertEquals(SAVED_JOURNEY_ID, response.getJourneyId());
    }

    @Test
    void secretariaCreaViajeAuxiliaryValido() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.ACTIVE);
        when(journeyService.existsActiveByCompanyIdAndAuxiliaryTruckPlate(COMPANY_A, "abc-999",
                SaveJourneyUseCase.ACTIVE_STATES)).thenReturn(false);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, requestAuxiliary(CONTAINER_ID, PROVIDER_ID,
                "  ABC-999  ", "  Juan Pérez  ", AuxiliaryReason.TRUCK_SHORTAGE, new BigDecimal("150.50")));

        ArgumentCaptor<Journey> captor = ArgumentCaptor.forClass(Journey.class);
        verify(journeyService).save(captor.capture());
        Journey saved = captor.getValue();
        assertEquals(JourneyState.PLANNED, saved.getJourneyState());
        assertEquals(JourneyExecutionType.AUXILIARY, saved.getExecutionType());
        assertNull(saved.getAssignmentId());
        assertEquals(PROVIDER_ID, saved.getAuxiliaryProviderId());
        assertEquals("ABC-999", saved.getAuxiliaryTruckPlate());
        assertEquals("Juan Pérez", saved.getAuxiliaryDriverName());
        assertEquals(AuxiliaryReason.TRUCK_SHORTAGE, saved.getAuxiliaryReason());
        assertEquals(new BigDecimal("150.50"), saved.getAgreedAmount());
        assertEquals("Origen", saved.getJourneyOrigin());
        assertEquals("Destino", saved.getJourneyDestination());
        assertEquals(SAVED_JOURNEY_ID, response.getJourneyId());
    }

    @Test
    void viajePartnerConDatosAuxiliaresDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);
        JourneyRequest request = requestPartner(CONTAINER_ID, ASSIGNMENT_ID);
        request.setAuxiliaryTruckPlate("ABC-999");

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A, request));

        verify(journeyService, never()).save(any());
    }

    @Test
    void viajePartnerSinAssignmentDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);

        assertThrows(BadRequestException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, null)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void viajeAuxiliaryConAssignmentDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);
        JourneyRequest request = requestAuxiliary(CONTAINER_ID, PROVIDER_ID, "ABC-999", "Juan Pérez",
                AuxiliaryReason.OTHER, BigDecimal.TEN);
        request.setAssignmentId(ASSIGNMENT_ID);

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A, request));

        verify(journeyService, never()).save(any());
    }

    @Test
    void viajeAuxiliarySinProviderDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, null, "ABC-999", "Juan Pérez", AuxiliaryReason.OTHER,
                        BigDecimal.TEN)));

        verify(auxiliaryProviderService, never()).findByCompanyIdAndProviderId(any(), any());
        verify(journeyService, never()).save(any());
    }

    @Test
    void assignmentDeOtraEmpresaDeberiaDevolver404() {
        stubSaveBase(COMPANY_A);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Asignación no encontrada con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, 999L)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void assignmentNoVigenteDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, TRUCK_ID, DRIVER_ID, LocalDateTime.now().plusDays(3), null));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void assignmentExpiradaDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, TRUCK_ID, DRIVER_ID, LocalDateTime.now().minusDays(30),
                        LocalDateTime.now().minusHours(1)));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void camionNoActivoDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, TRUCK_ID, DRIVER_ID, null, null));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, TruckStatus.MAINTENANCE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void conductorNoActivoDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, TRUCK_ID, DRIVER_ID, null, null));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, TruckStatus.ACTIVE));
        when(driverService.findByCompanyIdAndDriverId(COMPANY_A, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, DriverStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void solapamientoPorAsignacionDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, TRUCK_ID, DRIVER_ID, null, null));
        when(truckService.findByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, TruckStatus.ACTIVE));
        when(driverService.findByCompanyIdAndDriverId(COMPANY_A, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, DriverStatus.ACTIVE));
        when(journeyService.existsActiveByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID,
                SaveJourneyUseCase.ACTIVE_STATES)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void providerDeOtraEmpresaDeberiaDevolver404() {
        stubSaveBase(COMPANY_A);
        when(auxiliaryProviderService.findByCompanyIdAndProviderId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Proveedor de auxilio no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, 999L, "ABC-999", "Juan Pérez", AuxiliaryReason.OTHER,
                        BigDecimal.TEN)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void providerInactivoDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.INACTIVE);

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, PROVIDER_ID, "ABC-999", "Juan Pérez", AuxiliaryReason.OTHER,
                        BigDecimal.TEN)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void placaVaciaDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.ACTIVE);

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, PROVIDER_ID, "   ", "Juan Pérez", AuxiliaryReason.OTHER,
                        BigDecimal.TEN)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void nombreConductorVacioDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.ACTIVE);

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, PROVIDER_ID, "ABC-999", "  ", AuxiliaryReason.OTHER,
                        BigDecimal.TEN)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void reasonAusenteDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.ACTIVE);

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, PROVIDER_ID, "ABC-999", "Juan Pérez", null, BigDecimal.TEN)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void agreedAmountNegativoDeberiaDevolver400() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.ACTIVE);

        assertThrows(BadRequestException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, PROVIDER_ID, "ABC-999", "Juan Pérez", AuxiliaryReason.OTHER,
                        new BigDecimal("-5"))));

        verify(journeyService, never()).save(any());
    }

    @Test
    void solapamientoPorPlacaDeberiaDevolver409() {
        stubSaveBase(COMPANY_A);
        stubAuxiliaryProvider(COMPANY_A, AuxiliaryProviderStatus.ACTIVE);
        when(journeyService.existsActiveByCompanyIdAndAuxiliaryTruckPlate(COMPANY_A, "abc-999",
                SaveJourneyUseCase.ACTIVE_STATES)).thenReturn(true);

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A,
                requestAuxiliary(CONTAINER_ID, PROVIDER_ID, " ABC-999 ", "Juan Pérez", AuxiliaryReason.OTHER,
                        BigDecimal.TEN)));

        verify(journeyService).existsActiveByCompanyIdAndAuxiliaryTruckPlate(COMPANY_A, "abc-999",
                SaveJourneyUseCase.ACTIVE_STATES);
        verify(journeyService, never()).save(any());
    }

    @Test
    void contenedorDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        stubCompany(COMPANY_A, CompanyStatus.ACTIVE);
        when(containerService.findByCompanyIdAndContainerId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Contenedor no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(999L, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void cargaCanceladaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        stubCompany(COMPANY_A, CompanyStatus.ACTIVE);
        stubContainerAndCharge(COMPANY_A, ContainerState.LOADED, ChargeState.CANCELLED);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void contenedorDeliveredDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        stubCompany(COMPANY_A, CompanyStatus.ACTIVE);
        stubContainerAndCharge(COMPANY_A, ContainerState.DELIVERED, ChargeState.PENDING);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void empresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        stubCompany(COMPANY_A, CompanyStatus.INACTIVE);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeCrearViajeEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, requestPartner(CONTAINER_ID, ASSIGNMENT_ID)));

        verify(companyService, never()).findById(any());
        verify(journeyService, never()).save(any());
    }
}
