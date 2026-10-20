package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AssignTruckDriverUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long TRUCK_ID = 55L;
    private static final Long DRIVER_ID = 77L;
    private static final LocalDateTime FROM = LocalDateTime.of(2026, 5, 1, 8, 0);
    private static final LocalDateTime UNTIL = LocalDateTime.of(2026, 5, 31, 18, 0);

    @Mock
    private TruckDriverAssignmentService assignmentService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private AssignTruckDriverUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new AssignTruckDriverUseCase(assignmentService, companyService, currentUserProvider);
        authenticate(SECRETARY_USER_ID);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Long userId) {
        UserPrincipal principal = new UserPrincipal(userId, "cuenta@mail.com", "hash", UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
    }

    private void stubSuperAdmin() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of("SUPER_ADMIN"));
    }

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private Truck truck(Long truckId, Long companyId, Long ownerPartnerId, TruckStatus state) {
        Truck truck = new Truck();
        truck.setTruckId(truckId);
        truck.setCompanyId(companyId);
        truck.setOwnerPartnerId(ownerPartnerId);
        truck.setTruckState(state);
        return truck;
    }

    private Driver driver(Long driverId, Long companyId, DriverStatus state) {
        Driver driver = new Driver();
        driver.setDriverId(driverId);
        driver.setCompanyId(companyId);
        driver.setDriverState(state);
        return driver;
    }

    private TruckDriverAssignmentRequest request(Long truckId, Long driverId,
                                                 LocalDateTime from, LocalDateTime until) {
        TruckDriverAssignmentRequest request = new TruckDriverAssignmentRequest();
        request.setTruckId(truckId);
        request.setDriverId(driverId);
        request.setAssignedFrom(from);
        request.setAssignedUntil(until);
        return request;
    }

    private void stubHappyPath(Long companyId) {
        when(companyService.findById(companyId)).thenReturn(company(companyId, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(companyId, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, companyId, 100L, TruckStatus.ACTIVE));
        when(assignmentService.findDriverForUpdate(companyId, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, companyId, DriverStatus.ACTIVE));
        when(assignmentService.existsOverlappingForTruck(companyId, TRUCK_ID, FROM, UNTIL)).thenReturn(false);
        when(assignmentService.existsOverlappingForDriver(companyId, DRIVER_ID, FROM, UNTIL)).thenReturn(false);
        when(assignmentService.save(any(TruckDriverAssignment.class))).thenAnswer(invocation -> {
            TruckDriverAssignment assignment = invocation.getArgument(0);
            assignment.setAssignmentId(9L);
            return assignment;
        });
    }

    @Test
    void asignacionValidaDeberiaGuardarseSinModificarPropiedadDelCamion() {
        stubSecretaryOf(COMPANY_A);
        stubHappyPath(COMPANY_A);

        TruckDriverAssignmentResponse response =
                useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL));

        ArgumentCaptor<TruckDriverAssignment> captor = ArgumentCaptor.forClass(TruckDriverAssignment.class);
        verify(assignmentService).save(captor.capture());
        assertEquals(COMPANY_A, captor.getValue().getCompanyId());
        assertEquals(TRUCK_ID, captor.getValue().getTruckId());
        assertEquals(DRIVER_ID, captor.getValue().getDriverId());
        assertEquals(FROM, captor.getValue().getAssignedFrom());
        assertEquals(UNTIL, captor.getValue().getAssignedUntil());
        assertEquals(9L, response.getAssignmentId());
    }

    @Test
    void asignacionSinFechaFinalDeberiaQuedarAbierta() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, COMPANY_A, 100L, TruckStatus.ACTIVE));
        when(assignmentService.findDriverForUpdate(COMPANY_A, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE));
        when(assignmentService.existsOverlappingForTruck(anyLong(), anyLong(), any(), any())).thenReturn(false);
        when(assignmentService.existsOverlappingForDriver(anyLong(), anyLong(), any(), any())).thenReturn(false);
        when(assignmentService.save(any(TruckDriverAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TruckDriverAssignmentResponse response =
                useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, null));

        assertNull(response.getAssignedUntil());
    }

    @Test
    void secretariaDeEmpresaANoPuedeAsignarEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void superAdminPuedeAsignarEnCualquierEmpresa() {
        stubSuperAdmin();
        stubHappyPath(COMPANY_B);

        useCase.execute(COMPANY_B, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL));

        verify(assignmentService).save(any(TruckDriverAssignment.class));
    }

    @Test
    void camionDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Camión no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request(999L, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void conductorDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, COMPANY_A, 100L, TruckStatus.ACTIVE));
        when(assignmentService.findDriverForUpdate(COMPANY_A, 888L))
                .thenThrow(new ResourceNotFoundException("Conductor no encontrado con el id 888 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, 888L, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void fechasInvalidasHastaNoPosteriorAlInicioDeberianDevolver400() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));

        assertThrows(BadRequestException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, UNTIL, FROM)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void asignacionSolapadaDelCamionDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, COMPANY_A, 100L, TruckStatus.ACTIVE));
        when(assignmentService.findDriverForUpdate(COMPANY_A, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE));
        when(assignmentService.existsOverlappingForTruck(COMPANY_A, TRUCK_ID, FROM, UNTIL)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void asignacionSolapadaDelConductorDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, COMPANY_A, 100L, TruckStatus.ACTIVE));
        when(assignmentService.findDriverForUpdate(COMPANY_A, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, COMPANY_A, DriverStatus.ACTIVE));
        when(assignmentService.existsOverlappingForTruck(COMPANY_A, TRUCK_ID, FROM, UNTIL)).thenReturn(false);
        when(assignmentService.existsOverlappingForDriver(COMPANY_A, DRIVER_ID, FROM, UNTIL)).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void camionNoActivoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, COMPANY_A, 100L, TruckStatus.MAINTENANCE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void conductorInactivoDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findTruckForUpdate(COMPANY_A, TRUCK_ID))
                .thenReturn(truck(TRUCK_ID, COMPANY_A, 100L, TruckStatus.ACTIVE));
        when(assignmentService.findDriverForUpdate(COMPANY_A, DRIVER_ID))
                .thenReturn(driver(DRIVER_ID, COMPANY_A, DriverStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void empresaInactivaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.INACTIVE));

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void asignacionValidaDeberiaBloquearFilasDeCamionYConductor() {
        stubSecretaryOf(COMPANY_A);
        stubHappyPath(COMPANY_A);

        useCase.execute(COMPANY_A, request(TRUCK_ID, DRIVER_ID, FROM, UNTIL));

        verify(assignmentService).findTruckForUpdate(COMPANY_A, TRUCK_ID);
        verify(assignmentService).findDriverForUpdate(COMPANY_A, DRIVER_ID);
    }
}
