package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetJourneyByIdUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long PARTNER_USER_ID = 20L;
    private static final Long DRIVER_USER_ID = 30L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long PARTNER_A_ID = 100L;
    private static final Long DRIVER_A_ID = 200L;
    private static final Long JOURNEY_ID = 55L;

    @Mock
    private JourneyService journeyService;
    @Mock
    private PartnerService partnerService;
    @Mock
    private DriverService driverService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private GetJourneyByIdUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetJourneyByIdUseCase(journeyService, partnerService, driverService,
                companyService, currentUserProvider);
        authenticate(SECRETARY_USER_ID);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(Long userId) {
        UserPrincipal principal = new UserPrincipal(userId, "cuenta@mail.com", "hash",
                UserStatus.ACTIVE, List.of());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    private Company company(Long id) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        return company;
    }

    private Journey journey(Long id, Long companyId, JourneyState state, JourneyExecutionType executionType) {
        Journey journey = new Journey();
        journey.setJourneyId(id);
        journey.setCompanyId(companyId);
        journey.setJourneyOrigin("Origen " + id);
        journey.setJourneyDestination("Destino " + id);
        journey.setJourneyState(state);
        journey.setExecutionType(executionType);
        return journey;
    }

    private Partner partner(Long partnerId, Long userId) {
        Partner partner = new Partner();
        partner.setPartnerId(partnerId);
        partner.setCompanyId(COMPANY_A);
        partner.setUserId(userId);
        partner.setPartnerType(PartnerType.PERSON);
        partner.setPartnerName("Socio " + partnerId);
        partner.setPartnerState(PartnerStatus.ACTIVE);
        return partner;
    }

    private Driver driver(Long driverId, Long userId) {
        Driver driver = new Driver();
        driver.setDriverId(driverId);
        driver.setCompanyId(COMPANY_A);
        driver.setUserId(userId);
        driver.setDriverName("Chofer " + driverId);
        driver.setDriverLicense("LIC-" + driverId);
        driver.setDriverState(DriverStatus.ACTIVE);
        return driver;
    }

    private void stubSecretaryOf(Long companyId) {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, companyId)).thenReturn(true);
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, companyId)).thenReturn(List.of("SECRETARY"));
    }

    private void stubPartnerOf(Long companyId) {
        authenticate(PARTNER_USER_ID);
        when(userService.findPlatformRoleNames(PARTNER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(PARTNER_USER_ID, companyId)).thenReturn(true);
        when(userService.findCompanyRoleNames(PARTNER_USER_ID, companyId)).thenReturn(List.of("PARTNER"));
    }

    private void stubDriverOf(Long companyId) {
        authenticate(DRIVER_USER_ID);
        when(userService.findPlatformRoleNames(DRIVER_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(DRIVER_USER_ID, companyId)).thenReturn(true);
        when(userService.findCompanyRoleNames(DRIVER_USER_ID, companyId)).thenReturn(List.of("DRIVER"));
    }

    @Test
    void secretariaObtieneViajePorId() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, JOURNEY_ID))
                .thenReturn(journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER));

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID);

        assertEquals(JOURNEY_ID, response.getJourneyId());
        assertEquals(COMPANY_A, response.getCompanyId());
        assertEquals(JourneyState.IN_PROGRESS, response.getJourneyState());
        assertEquals(JourneyExecutionType.PARTNER, response.getExecutionType());
        verify(partnerService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
        verify(driverService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void viajeDeOtraEmpresaDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException(
                        "Viaje no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, 999L));
    }

    @Test
    void partnerObtieneViajeDeSuCamion() {
        stubPartnerOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.of(partner(PARTNER_A_ID, PARTNER_USER_ID)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndOwnerPartnerId(
                COMPANY_A, JOURNEY_ID, PARTNER_A_ID))
                .thenReturn(Optional.of(
                        journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER)));

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID);

        assertEquals(JOURNEY_ID, response.getJourneyId());
        assertEquals(JourneyState.PLANNED, response.getJourneyState());
        verify(journeyService, never()).findByCompanyIdAndJourneyId(anyLong(), anyLong());
    }

    @Test
    void partnerNoVeViajeAjenoDeberiaDevolver404() {
        stubPartnerOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.of(partner(PARTNER_A_ID, PARTNER_USER_ID)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndOwnerPartnerId(
                COMPANY_A, JOURNEY_ID, PARTNER_A_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID));
    }

    @Test
    void driverObtieneSuViajeAsignado() {
        stubDriverOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, DRIVER_USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_A_ID, DRIVER_USER_ID)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(
                COMPANY_A, JOURNEY_ID, DRIVER_A_ID))
                .thenReturn(Optional.of(
                        journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER)));

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID);

        assertEquals(JOURNEY_ID, response.getJourneyId());
        assertEquals(JourneyState.IN_PROGRESS, response.getJourneyState());
        verify(journeyService, never()).findByCompanyIdAndJourneyId(anyLong(), anyLong());
    }

    @Test
    void driverNoVeViajeAjenoDeberiaDevolver404() {
        stubDriverOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, DRIVER_USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_A_ID, DRIVER_USER_ID)));
        when(journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(
                COMPANY_A, JOURNEY_ID, DRIVER_A_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID));
    }

    @Test
    void secretariaDeEmpresaANoPuedeVerEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, COMPANY_B)).thenReturn(false);

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, JOURNEY_ID));

        verify(journeyService, never()).findByCompanyIdAndJourneyId(anyLong(), anyLong());
        verify(companyService, never()).findById(anyLong());
    }
}
