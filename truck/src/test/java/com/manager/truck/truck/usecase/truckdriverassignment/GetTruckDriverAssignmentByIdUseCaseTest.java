package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetTruckDriverAssignmentByIdUseCaseTest {

    private static final Long USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long ASSIGNMENT_ID = 9L;
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

    private GetTruckDriverAssignmentByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetTruckDriverAssignmentByIdUseCase(
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

    private TruckDriverAssignment assignment() {
        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setAssignmentId(ASSIGNMENT_ID);
        assignment.setCompanyId(COMPANY_A);
        assignment.setTruckId(TRUCK_ID);
        assignment.setDriverId(DRIVER_ID);
        assignment.setAssignedFrom(LocalDateTime.of(2026, 5, 1, 8, 0));
        return assignment;
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

    private Truck truckWithOwner(Long ownerPartnerId) {
        Truck truck = new Truck();
        truck.setTruckId(TRUCK_ID);
        truck.setCompanyId(COMPANY_A);
        truck.setOwnerPartnerId(ownerPartnerId);
        truck.setTruckState(TruckStatus.ACTIVE);
        return truck;
    }

    private Driver driverWithUser() {
        Driver driver = new Driver();
        driver.setDriverId(PROFILE_DRIVER_ID);
        driver.setCompanyId(COMPANY_A);
        driver.setUserId(USER_ID);
        return driver;
    }

    @Test
    void secretariaConsultaCualquierAsignacion() {
        stubRoles("SECRETARY");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID)).thenReturn(assignment());

        TruckDriverAssignmentResponse response = useCase.execute(COMPANY_A, ASSIGNMENT_ID);

        assertEquals(ASSIGNMENT_ID, response.getAssignmentId());
    }

    @Test
    void partnerConsultaAsignacionDeSuCamion() {
        stubRoles("PARTNER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID)).thenReturn(assignment());
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, USER_ID)).thenReturn(Optional.of(partnerWithUser()));
        when(assignmentService.findTruckByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID))
                .thenReturn(Optional.of(truckWithOwner(PARTNER_ID)));

        TruckDriverAssignmentResponse response = useCase.execute(COMPANY_A, ASSIGNMENT_ID);

        assertEquals(ASSIGNMENT_ID, response.getAssignmentId());
    }

    @Test
    void partnerNoPuedeConsultarAsignacionDeCamionAjeno() {
        stubRoles("PARTNER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID)).thenReturn(assignment());
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, USER_ID)).thenReturn(Optional.of(partnerWithUser()));
        when(assignmentService.findTruckByCompanyIdAndTruckId(COMPANY_A, TRUCK_ID))
                .thenReturn(Optional.of(truckWithOwner(999L)));

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, ASSIGNMENT_ID));
    }

    @Test
    void driverConsultaSuPropiaAsignacion() {
        stubRoles("DRIVER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        TruckDriverAssignment mine = assignment();
        mine.setDriverId(PROFILE_DRIVER_ID);
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID)).thenReturn(mine);
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driverWithUser()));

        TruckDriverAssignmentResponse response = useCase.execute(COMPANY_A, ASSIGNMENT_ID);

        assertEquals(PROFILE_DRIVER_ID, response.getDriverId());
    }

    @Test
    void driverNoPuedeConsultarAsignacionAjena() {
        stubRoles("DRIVER");
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID)).thenReturn(assignment());
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, USER_ID))
                .thenReturn(Optional.of(driverWithUser()));

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_A, ASSIGNMENT_ID));
    }

    @Test
    void asignacionInexistenteDeberiaDevolver404() {
        when(userService.findPlatformRoleNames(USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(USER_ID, COMPANY_A)).thenReturn(true);
        when(companyService.findById(COMPANY_A)).thenReturn(company());
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Asignación no encontrada con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, 999L));
    }
}
