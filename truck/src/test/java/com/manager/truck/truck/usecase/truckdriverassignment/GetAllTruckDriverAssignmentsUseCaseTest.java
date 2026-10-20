package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentsResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllTruckDriverAssignmentsUseCaseTest {

    private static final Long USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long TRUCK_ID = 55L;
    private static final Long DRIVER_ID = 77L;
    private static final Long PARTNER_ID = 100L;
    private static final Long PROFILE_DRIVER_ID = 200L;

    @Mock
    private TruckDriverAssignmentService assignmentService;
    @Mock
    private PartnerService partnerService;
    @Mock
    private DriverService driverService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private GetAllTruckDriverAssignmentsUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllTruckDriverAssignmentsUseCase(
                assignmentService, partnerService, driverService, companyService, currentUserProvider);
        authenticate(USER_ID);
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

    private void stubRoles(String... companyRoles) {
        when(userService.findPlatformRoleNames(USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(USER_ID, COMPANY_A)).thenReturn(true);
        when(userService.findCompanyRoleNames(USER_ID, COMPANY_A)).thenReturn(List.of(companyRoles));
    }

    private Company company() {
        Company company = new Company();
        company.setCompanyId(COMPANY_A);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    private TruckDriverAssignment assignment(Long assignmentId, Long truckId, Long driverId) {
        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setAssignmentId(assignmentId);
        assignment.setCompanyId(COMPANY_A);
        assignment.setTruckId(truckId);
        assignment.setDriverId(driverId);
        assignment.setAssignedFrom(LocalDateTime.of(2026, 5, 1, 8, 0));
        return assignment;
    }

    private Page<TruckDriverAssignment> pageOf(TruckDriverAssignment... assignments) {
        return new PageImpl<>(List.of(assignments));
    }

    private Partner partnerWithUser() {
        Partner partner = new Partner();
        partner.setPartnerId(PARTNER_ID);
        partner.setCompanyId(COMPANY_A);
        partner.setPartnerType(PartnerType.PERSON);
        partner.setPartnerName("Socio");
        partner.setPartnerState(PartnerStatus.ACTIVE);
        partner.setUserId(USER_ID);
        return partner;
    }

    private Driver driverWithUser() {
        Driver driver = new Driver();
        driver.setDriverId(PROFILE_DRIVER_ID);
        driver.setCompanyId(COMPANY_A);
        driver.setUserId(USER_ID);
        return driver;
    }

    @Test
    void secretariaVeTodoElHistorial() {
        stubRoles("SECRETARY");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyId(anyLong(), any(Pageable.class)))
                .thenReturn(pageOf(assignment(1L, TRUCK_ID, DRIVER_ID)));

        TruckDriverAssignmentsResponse response = useCase.execute(COMPANY_A, null, null, Pageable.unpaged());

        assertEquals(1, response.getAssignments().size());
        assertEquals(1L, response.getAssignments().get(0).getAssignmentId());
    }

    @Test
    void secretariaPuedeFiltrarPorCamionYConductor() {
        stubRoles("SECRETARY");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyIdAndTruckIdAndDriverId(COMPANY_A, TRUCK_ID, DRIVER_ID,
                Pageable.unpaged())).thenReturn(pageOf(assignment(1L, TRUCK_ID, DRIVER_ID)));

        TruckDriverAssignmentsResponse response =
                useCase.execute(COMPANY_A, TRUCK_ID, DRIVER_ID, Pageable.unpaged());

        assertEquals(1, response.getAssignments().size());
        verify(assignmentService).findByCompanyIdAndTruckIdAndDriverId(COMPANY_A, TRUCK_ID, DRIVER_ID,
                Pageable.unpaged());
    }

    @Test
    void partnerVeSoloLasAsignacionesDeSusCamiones() {
        stubRoles("PARTNER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, USER_ID)).thenReturn(Optional.of(partnerWithUser()));
        when(assignmentService.findByCompanyIdAndOwnerPartnerId(COMPANY_A, PARTNER_ID, Pageable.unpaged()))
                .thenReturn(pageOf(assignment(1L, TRUCK_ID, DRIVER_ID)));

        TruckDriverAssignmentsResponse response = useCase.execute(COMPANY_A, null, null, Pageable.unpaged());

        assertEquals(1, response.getAssignments().size());
        verify(assignmentService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void partnerSinPerfilDevuelveVacio() {
        stubRoles("PARTNER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, USER_ID)).thenReturn(Optional.empty());

        TruckDriverAssignmentsResponse response = useCase.execute(COMPANY_A, null, null, Pageable.unpaged());

        assertTrue(response.getAssignments().isEmpty());
    }

    @Test
    void driverVeSoloSusAsignaciones() {
        stubRoles("DRIVER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driverWithUser()));
        when(assignmentService.findByCompanyIdAndDriverId(COMPANY_A, PROFILE_DRIVER_ID, Pageable.unpaged()))
                .thenReturn(pageOf(assignment(1L, TRUCK_ID, PROFILE_DRIVER_ID)));

        TruckDriverAssignmentsResponse response = useCase.execute(COMPANY_A, null, null, Pageable.unpaged());

        assertEquals(1, response.getAssignments().size());
        assertEquals(PROFILE_DRIVER_ID, response.getAssignments().get(0).getDriverId());
    }

    @Test
    void usuarioSinRolesRelevantesDeberiaDevolver403() {
        stubRoles();
        when(companyService.findById(COMPANY_A)).thenReturn(company());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, null, null, Pageable.unpaged()));
    }

    @Test
    void usuarioDeOtraEmpresaDeberiaDevolver403() {
        when(userService.findPlatformRoleNames(USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(USER_ID, COMPANY_A)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, null, null, Pageable.unpaged()));
    }
}
