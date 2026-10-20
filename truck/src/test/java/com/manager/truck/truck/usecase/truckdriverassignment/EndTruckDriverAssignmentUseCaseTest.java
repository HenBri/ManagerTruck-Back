package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentEndRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EndTruckDriverAssignmentUseCaseTest {

    private static final Long SECRETARY_USER_ID = 10L;
    private static final Long COMPANY_A = 1L;
    private static final Long COMPANY_B = 2L;
    private static final Long ASSIGNMENT_ID = 9L;
    private static final LocalDateTime FROM = LocalDateTime.of(2026, 5, 1, 8, 0);
    private static final LocalDateTime NEW_UNTIL = LocalDateTime.of(2026, 5, 31, 18, 0);

    @Mock
    private TruckDriverAssignmentService assignmentService;
    @Mock
    private CompanyService companyService;
    @Mock
    private UserService userService;

    private CurrentUserProvider currentUserProvider;

    private EndTruckDriverAssignmentUseCase useCase;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider(userService);
        useCase = new EndTruckDriverAssignmentUseCase(assignmentService, companyService, currentUserProvider);
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

    private Company company(Long id, CompanyStatus status) {
        Company company = new Company();
        company.setCompanyId(id);
        company.setCompanyName("Empresa " + id);
        company.setCompanyStatus(status);
        return company;
    }

    private TruckDriverAssignment openAssignment(Long companyId) {
        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setAssignmentId(ASSIGNMENT_ID);
        assignment.setCompanyId(companyId);
        assignment.setTruckId(55L);
        assignment.setDriverId(77L);
        assignment.setAssignedFrom(FROM);
        assignment.setAssignedUntil(null);
        return assignment;
    }

    private TruckDriverAssignmentEndRequest endRequest(LocalDateTime until) {
        TruckDriverAssignmentEndRequest request = new TruckDriverAssignmentEndRequest();
        request.setAssignedUntil(until);
        return request;
    }

    @Test
    void finalizacionValidaDeberiaConservarLaFilaConFechaDeFin() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(openAssignment(COMPANY_A));
        when(assignmentService.save(any(TruckDriverAssignment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TruckDriverAssignmentResponse response =
                useCase.execute(COMPANY_A, ASSIGNMENT_ID, endRequest(NEW_UNTIL));

        ArgumentCaptor<TruckDriverAssignment> captor = ArgumentCaptor.forClass(TruckDriverAssignment.class);
        verify(assignmentService).save(captor.capture());
        assertEquals(ASSIGNMENT_ID, captor.getValue().getAssignmentId());
        assertEquals(NEW_UNTIL, response.getAssignedUntil());
    }

    @Test
    void asignacionYaFinalizadaDeberiaDevolver409() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        TruckDriverAssignment closed = openAssignment(COMPANY_A);
        closed.setAssignedUntil(FROM.plusDays(10));
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID)).thenReturn(closed);

        assertThrows(ConflictException.class,
                () -> useCase.execute(COMPANY_A, ASSIGNMENT_ID, endRequest(NEW_UNTIL)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void fechaDeFinNoPosteriorAlInicioDeberiaDevolver400() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, ASSIGNMENT_ID))
                .thenReturn(openAssignment(COMPANY_A));

        assertThrows(BadRequestException.class,
                () -> useCase.execute(COMPANY_A, ASSIGNMENT_ID, endRequest(FROM)));

        verify(assignmentService, never()).save(any());
    }

    @Test
    void asignacionInexistenteEnElTenantDeberiaDevolver404() {
        stubSecretaryOf(COMPANY_A);
        when(companyService.findById(COMPANY_A)).thenReturn(company(COMPANY_A, CompanyStatus.ACTIVE));
        when(assignmentService.findByCompanyIdAndAssignmentId(COMPANY_A, 999L))
                .thenThrow(new ResourceNotFoundException("Asignación no encontrada con el id 999 en la empresa 1"));

        assertThrows(ResourceNotFoundException.class,
                () -> useCase.execute(COMPANY_A, 999L, endRequest(NEW_UNTIL)));
    }

    @Test
    void secretariaDeEmpresaANoPuedeFinalizarEnLaEmpresaB() {
        when(userService.findPlatformRoleNames(SECRETARY_USER_ID)).thenReturn(List.of());
        when(userService.findCompanyRoleNames(SECRETARY_USER_ID, COMPANY_B)).thenReturn(List.of());

        assertThrows(ForbiddenOperationException.class,
                () -> useCase.execute(COMPANY_B, ASSIGNMENT_ID, endRequest(NEW_UNTIL)));

        verify(assignmentService, never()).save(any());
    }
}
