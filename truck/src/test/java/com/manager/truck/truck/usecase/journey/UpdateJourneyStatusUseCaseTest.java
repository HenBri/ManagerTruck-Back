package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.JourneyStatusRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateJourneyStatusUseCaseTest {

    private static final Long USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long JOURNEY_ID = 500L;
    private static final Long ASSIGNMENT_ID = 400L;
    private static final Long DRIVER_ID = 77L;

    @Mock
    private JourneyService journeyService;
    @Mock
    private DriverService driverService;
    @Mock
    private TruckDriverAssignmentService assignmentService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateJourneyStatusUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateJourneyStatusUseCase(journeyService, driverService, assignmentService,
                companyService, currentUserProvider);
        UserPrincipal principal = new UserPrincipal(USER_ID, "cuenta@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(USER_ID, companyId)).thenReturn(true);
        when(userService.findCompanyRoleNames(USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
    }

    private void stubDriverOf(Long companyId) {
        when(userService.findPlatformRoleNames(USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(USER_ID, companyId)).thenReturn(true);
        when(userService.findCompanyRoleNames(USER_ID, companyId)).thenReturn(List.of("DRIVER"));
    }

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private Driver driver(Long id, Long companyId, DriverStatus state) {
        Driver driver = new Driver();
        driver.setDriverId(id);
        driver.setCompanyId(companyId);
        driver.setDriverState(state);
        return driver;
    }

    private TruckDriverAssignment assignment(Long id, LocalDateTime from, LocalDateTime until) {
        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setAssignmentId(id);
        assignment.setCompanyId(COMPANY_A);
        assignment.setAssignedFrom(from != null ? from : LocalDateTime.now().minusDays(30));
        assignment.setAssignedUntil(until);
        return assignment;
    }

    private Journey journey(Long id, Long companyId, JourneyState state, JourneyExecutionType type) {
        Journey journey = new Journey();
        journey.setJourneyId(id);
        journey.setCompanyId(companyId);
        journey.setContainerId(100L);
        journey.setJourneyOrigin("Origen");
        journey.setJourneyDestination("Destino");
        journey.setJourneyState(state);
        journey.setExecutionType(type);
        if (type == JourneyExecutionType.PARTNER) {
            journey.setAssignmentId(ASSIGNMENT_ID);
        }
        return journey;
    }

    private JourneyStatusRequest statusRequest(JourneyState state) {
        JourneyStatusRequest request = new JourneyStatusRequest();
        request.setStatus(state);
        return request;
    }

    private void stubCompanyFound(Long companyId) {
        when(companyService.findById(companyId)).thenReturn(company(companyId, CompanyStatus.ACTIVE));
    }

    private void stubAdminJourney(Long companyId, Journey journey) {
        stubSecretaryOf(companyId);
        stubCompanyFound(companyId);
        if (journey != null) {
            when(journeyService.findByCompanyIdAndJourneyId(companyId, JOURNEY_ID)).thenReturn(journey);
        }
    }

    private void stubSave() {
        when(journeyService.save(any(Journey.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void plannedAInProgressFijaFechaInicio() {
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER);
        stubAdminJourney(COMPANY_A, journey);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, null, null));
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS));

        assertEquals(JourneyState.IN_PROGRESS, response.getJourneyState());
        assertNotNull(response.getJourneyStartDate());
        assertNull(response.getJourneyEndDate());
        verify(journeyService).save(journey);
    }

    @Test
    void inProgressACompletedFijaFechaFin() {
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER);
        journey.setJourneyStartDate(LocalDateTime.now().minusHours(2));
        stubAdminJourney(COMPANY_A, journey);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.COMPLETED));

        assertEquals(JourneyState.COMPLETED, response.getJourneyState());
        assertNotNull(response.getJourneyEndDate());
        assertTrue(response.getJourneyEndDate().isAfter(response.getJourneyStartDate()));
        verify(journeyService).save(journey);
    }

    @Test
    void completedDesdeInProgressSinFechaInicioNoLanza() {
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER);
        stubAdminJourney(COMPANY_A, journey);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.COMPLETED));

        assertEquals(JourneyState.COMPLETED, response.getJourneyState());
        assertNotNull(response.getJourneyEndDate());
        assertNull(response.getJourneyStartDate());
    }

    @Test
    void plannedACancelledPermitido() {
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER);
        stubAdminJourney(COMPANY_A, journey);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.CANCELLED));

        assertEquals(JourneyState.CANCELLED, response.getJourneyState());
        assertNull(response.getJourneyStartDate());
        assertNull(response.getJourneyEndDate());
        verify(journeyService).save(journey);
    }

    @Test
    void inProgressACancelledPermitido() {
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER);
        journey.setJourneyStartDate(LocalDateTime.now().minusHours(1));
        stubAdminJourney(COMPANY_A, journey);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.CANCELLED));

        assertEquals(JourneyState.CANCELLED, response.getJourneyState());
        assertNull(response.getJourneyEndDate());
        verify(journeyService).save(journey);
    }

    @Test
    void completedEsTerminalDeberiaDevolver409() {
        stubAdminJourney(COMPANY_A, journey(JOURNEY_ID, COMPANY_A, JourneyState.COMPLETED,
                JourneyExecutionType.PARTNER));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void cancelledEsTerminalDeberiaDevolver409() {
        stubAdminJourney(COMPANY_A, journey(JOURNEY_ID, COMPANY_A, JourneyState.CANCELLED,
                JourneyExecutionType.PARTNER));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void retrocesoInProgressAPlannedDeberiaDevolver409() {
        stubAdminJourney(COMPANY_A, journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS,
                JourneyExecutionType.PARTNER));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.PLANNED)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void saltoPlannedACompletedDeberiaDevolver409() {
        stubAdminJourney(COMPANY_A, journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED,
                JourneyExecutionType.PARTNER));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.COMPLETED)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void mismoEstadoDeberiaDevolver409() {
        stubAdminJourney(COMPANY_A, journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED,
                JourneyExecutionType.PARTNER));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.PLANNED)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void driverPuedeIniciarSuViajePartner() {
        stubDriverOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE)));
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER);
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(COMPANY_A, JOURNEY_ID, DRIVER_ID))
                .thenReturn(Optional.of(journey));
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, null, null));
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS));

        assertEquals(JourneyState.IN_PROGRESS, response.getJourneyState());
        assertNotNull(response.getJourneyStartDate());
        verify(journeyService).save(journey);
    }

    @Test
    void driverPuedeCompletarSuViajePartner() {
        stubDriverOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE)));
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER);
        journey.setJourneyStartDate(LocalDateTime.now().minusHours(1));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(COMPANY_A, JOURNEY_ID, DRIVER_ID))
                .thenReturn(Optional.of(journey));
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.COMPLETED));

        assertEquals(JourneyState.COMPLETED, response.getJourneyState());
        assertNotNull(response.getJourneyEndDate());
        verify(journeyService).save(journey);
    }

    @Test
    void driverNoPuedeCancelarDeberiaDevolver403() {
        stubDriverOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(COMPANY_A, JOURNEY_ID, DRIVER_ID))
                .thenReturn(Optional.of(journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED,
                        JourneyExecutionType.PARTNER)));

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.CANCELLED)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void driverNoPuedeVerViajeAjenoDeberiaDevolver404() {
        stubDriverOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(COMPANY_A, JOURNEY_ID, DRIVER_ID))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void driverSinConductorVinculadoDeberiaDevolver403() {
        stubDriverOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID)).thenReturn(Optional.empty());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).findOptionalByCompanyIdAndJourneyIdAndDriverId(any(), any(), any());
        verify(journeyService, never()).save(any());
    }

    @Test
    void driverNoPuedeActualizarViajeAuxiliaryDeberiaDevolver403() {
        stubDriverOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(COMPANY_A, JOURNEY_ID, DRIVER_ID))
                .thenReturn(Optional.of(journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED,
                        JourneyExecutionType.AUXILIARY)));

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void asignacionExpiradaNoPermiteIniciarDeberiaDevolver409() {
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER);
        stubAdminJourney(COMPANY_A, journey);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(assignment(ASSIGNMENT_ID, LocalDateTime.now().minusDays(30),
                        LocalDateTime.now().minusHours(1)));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void viajeDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        stubCompanyFound(COMPANY_A);
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Viaje no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, 999L,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(journeyService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeActualizarEstadoEnEmpresaB() {
        when(userService.findPlatformRoleNames(USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(USER_ID, COMPANY_B)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, JOURNEY_ID,
                statusRequest(JourneyState.IN_PROGRESS)));

        verify(companyService, never()).findById(any());
        verify(journeyService, never()).save(any());
    }
}
