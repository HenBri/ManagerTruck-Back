package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.dto.request.JourneyUpdateRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.UserPrincipal;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.JourneyService;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UpdateJourneyUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long JOURNEY_ID = 500L;

    @Mock
    private JourneyService journeyService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;
    private UpdateJourneyUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new UpdateJourneyUseCase(journeyService, companyService, currentUserProvider);
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

    private Journey journey(Long id, Long companyId, JourneyState state) {
        Journey journey = new Journey();
        journey.setJourneyId(id);
        journey.setCompanyId(companyId);
        journey.setContainerId(100L);
        journey.setJourneyOrigin("Origen");
        journey.setJourneyDestination("Destino");
        journey.setJourneyDescription("Descripción");
        journey.setJourneyType("FLETE");
        journey.setJourneyState(state);
        journey.setExecutionType(JourneyExecutionType.PARTNER);
        journey.setAssignmentId(400L);
        return journey;
    }

    private JourneyUpdateRequest request(String origin, String destination, String description, String type) {
        JourneyUpdateRequest request = new JourneyUpdateRequest();
        request.setJourneyOrigin(origin);
        request.setJourneyDestination(destination);
        request.setJourneyDescription(description);
        request.setJourneyType(type);
        return request;
    }

    private void stubUpdateBase(Long companyId) {
        stubSecretaryOf(companyId);
        when(companyService.findById(companyId)).thenReturn(company(companyId, CompanyStatus.ACTIVE));
    }

    private void stubSave() {
        when(journeyService.save(any(Journey.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void secretariaActualizaViajePlanned() {
        stubUpdateBase(COMPANY_A);
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED);
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, JOURNEY_ID)).thenReturn(journey);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                request("  Norte  ", " Sur ", "  Detalle nuevo  ", " FLETE URBANO "));

        ArgumentCaptor<Journey> captor = ArgumentCaptor.forClass(Journey.class);
        verify(journeyService).save(captor.capture());
        Journey saved = captor.getValue();
        assertEquals("Norte", saved.getJourneyOrigin());
        assertEquals("Sur", saved.getJourneyDestination());
        assertEquals("Detalle nuevo", saved.getJourneyDescription());
        assertEquals("FLETE URBANO", saved.getJourneyType());
        assertEquals("Norte", response.getJourneyOrigin());
        assertEquals("Sur", response.getJourneyDestination());
        assertEquals(JourneyState.PLANNED, response.getJourneyState());
    }

    @Test
    void viajeInProgressNoSePuedeEditarDeberiaDevolver409() {
        stubUpdateBase(COMPANY_A);
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, JOURNEY_ID))
                .thenReturn(journey(JOURNEY_ID, COMPANY_A, JourneyState.IN_PROGRESS));

        assertThrows(ConflictException.class, () -> useCase.execute(COMPANY_A, JOURNEY_ID,
                request("Norte", "Sur", "Detalle", "FLETE")));

        verify(journeyService, never()).save(any());
    }

    @Test
    void viajeNoEncontradoDeberiaDevolver404() {
        stubUpdateBase(COMPANY_A);
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Viaje no encontrado con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class, () -> useCase.execute(COMPANY_A, 999L,
                request("Norte", "Sur", "Detalle", "FLETE")));

        verify(journeyService, never()).save(any());
    }

    @Test
    void secretariaDeEmpresaANoPuedeActualizarEnEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class, () -> useCase.execute(COMPANY_B, JOURNEY_ID,
                request("Norte", "Sur", "Detalle", "FLETE")));

        verify(companyService, never()).findById(any());
        verify(journeyService, never()).findByCompanyIdAndJourneyId(any(), any());
        verify(journeyService, never()).save(any());
    }

    @Test
    void camposVaciosDeDescripcionSeGuardanComoNull() {
        stubUpdateBase(COMPANY_A);
        Journey journey = journey(JOURNEY_ID, COMPANY_A, JourneyState.PLANNED);
        when(journeyService.findByCompanyIdAndJourneyId(COMPANY_A, JOURNEY_ID)).thenReturn(journey);
        stubSave();

        JourneyResponse response = useCase.execute(COMPANY_A, JOURNEY_ID,
                request("Norte", "Sur", "   ", ""));

        assertNull(response.getJourneyDescription());
        assertNull(response.getJourneyType());
    }
}
