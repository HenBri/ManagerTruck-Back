package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

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
class GetAllJourneysUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long PARTNER_USER_ID = 20L;
    private static final Long DRIVER_USER_ID = 30L;
    private static final Long COMPANY_A = 1L;
    private static final Long PARTNER_A_ID = 100L;
    private static final Long DRIVER_A_ID = 200L;

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

    private GetAllJourneysUseCase useCase;

    private final Pageable pageable = PageRequest.of(0, 20);

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new GetAllJourneysUseCase(journeyService, partnerService, driverService,
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
    void secretariaListaTodosLosViajesSinFiltros() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Journey first = journey(501L, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER);
        Journey second = journey(502L, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.AUXILIARY);
        when(journeyService.findByCompanyId(COMPANY_A, pageable))
                .thenReturn(new PageImpl<>(List.of(first, second), PageRequest.of(0, 20), 2));

        JourneysResponse response = useCase.execute(COMPANY_A, null, null, null, pageable);

        assertEquals(2, response.getJourneys().size());
        assertEquals(2, response.getTotalElements());
        assertEquals(501L, response.getJourneys().get(0).getJourneyId());
        assertEquals(502L, response.getJourneys().get(1).getJourneyId());
        verify(partnerService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
        verify(driverService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
    }

    @Test
    void secretariaFiltraPorEstadoYModalidad() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        Journey filtered = journey(503L, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER);
        when(journeyService.findByCompanyIdAndJourneyStateAndExecutionType(
                COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER, pageable))
                .thenReturn(new PageImpl<>(List.of(filtered), PageRequest.of(0, 20), 1));

        JourneysResponse response = useCase.execute(COMPANY_A, null, JourneyState.IN_PROGRESS,
                JourneyExecutionType.PARTNER, pageable);

        assertEquals(1, response.getJourneys().size());
        assertEquals(503L, response.getJourneys().get(0).getJourneyId());
        assertEquals(JourneyState.IN_PROGRESS, response.getJourneys().get(0).getJourneyState());
        assertEquals(JourneyExecutionType.PARTNER, response.getJourneys().get(0).getExecutionType());
        verify(journeyService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void partnerSoloListaViajesDeSusCamiones() {
        stubPartnerOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID))
                .thenReturn(Optional.of(partner(PARTNER_A_ID, PARTNER_USER_ID)));
        Journey own = journey(504L, COMPANY_A, JourneyState.PLANNED, JourneyExecutionType.PARTNER);
        when(journeyService.findByCompanyIdAndOwnerPartnerId(COMPANY_A, PARTNER_A_ID, null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(own), PageRequest.of(0, 20), 1));

        JourneysResponse response = useCase.execute(COMPANY_A, null, null, null, pageable);

        assertEquals(1, response.getJourneys().size());
        assertEquals(504L, response.getJourneys().get(0).getJourneyId());
        assertEquals(1, response.getTotalElements());
        verify(journeyService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void partnerSinPartnerVinculadoObtienePaginaVacia() {
        stubPartnerOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(partnerService.findByCompanyIdAndUserId(COMPANY_A, PARTNER_USER_ID)).thenReturn(Optional.empty());

        JourneysResponse response = useCase.execute(COMPANY_A, null, null, null, pageable);

        assertTrue(response.getJourneys().isEmpty());
        assertEquals(0, response.getTotalElements());
        verify(journeyService, never())
                .findByCompanyIdAndOwnerPartnerId(anyLong(), anyLong(), any(), any(), any(), any());
    }

    @Test
    void driverSoloListaSusViajesAsignados() {
        stubDriverOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, DRIVER_USER_ID))
                .thenReturn(Optional.of(driver(DRIVER_A_ID, DRIVER_USER_ID)));
        Journey assigned = journey(505L, COMPANY_A, JourneyState.IN_PROGRESS, JourneyExecutionType.PARTNER);
        when(journeyService.findByCompanyIdAndDriverId(COMPANY_A, DRIVER_A_ID, null, null, null, pageable))
                .thenReturn(new PageImpl<>(List.of(assigned), PageRequest.of(0, 20), 1));

        JourneysResponse response = useCase.execute(COMPANY_A, null, null, null, pageable);

        assertEquals(1, response.getJourneys().size());
        assertEquals(505L, response.getJourneys().get(0).getJourneyId());
        assertEquals(1, response.getTotalElements());
        verify(journeyService, never()).findByCompanyId(anyLong(), any(Pageable.class));
    }

    @Test
    void driverSinConductorVinculadoObtienePaginaVacia() {
        stubDriverOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));
        when(driverService.findByCompanyIdAndUserId(COMPANY_A, DRIVER_USER_ID)).thenReturn(Optional.empty());

        JourneysResponse response = useCase.execute(COMPANY_A, null, null, null, pageable);

        assertTrue(response.getJourneys().isEmpty());
        assertEquals(0, response.getTotalElements());
        verify(journeyService, never())
                .findByCompanyIdAndDriverId(anyLong(), anyLong(), any(), any(), any(), any());
    }

    @Test
    void usuarioSinRolesDeberiaDevolver403() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.isActiveMember(SECRETARY_USER_ID, COMPANY_A)).thenReturn(true);
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_A)).thenReturn(List.of());
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A));

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_A, null, null, null, pageable));

        verify(journeyService, never()).findByCompanyId(anyLong(), any(Pageable.class));
        verify(partnerService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
        verify(driverService, never()).findByCompanyIdAndUserId(anyLong(), anyLong());
    }
}
