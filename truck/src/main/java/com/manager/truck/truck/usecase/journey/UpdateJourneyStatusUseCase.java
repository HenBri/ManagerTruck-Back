package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.JourneyStatusRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateJourneyStatusUseCase {

    private static final Map<JourneyState, Set<JourneyState>> ALLOWED_TRANSITIONS = Map.of(
            JourneyState.PLANNED, Set.of(JourneyState.IN_PROGRESS, JourneyState.CANCELLED),
            JourneyState.IN_PROGRESS, Set.of(JourneyState.COMPLETED, JourneyState.CANCELLED),
            JourneyState.COMPLETED, Set.of(),
            JourneyState.CANCELLED, Set.of());

    private static final Set<JourneyState> DRIVER_ALLOWED_TARGETS =
            Set.of(JourneyState.IN_PROGRESS, JourneyState.COMPLETED);

    private final JourneyService journeyService;
    private final DriverService driverService;
    private final TruckDriverAssignmentService assignmentService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JourneyResponse execute(Long companyId, Long journeyId, JourneyStatusRequest request) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Journey journey = findAuthorizedJourney(companyId, journeyId);
        JourneyState current = journey.getJourneyState();
        JourneyState target = request.getStatus();
        if (current == target) {
            throw new ConflictException("El viaje ya se encuentra en el estado " + current);
        }
        if (!ALLOWED_TRANSITIONS.get(current).contains(target)) {
            throw new ConflictException(
                    "Transición de estado inválida para el viaje: " + current + " -> " + target);
        }
        if (!isAdmin(companyId)) {
            enforceDriverRules(companyId, journey, target);
        }
        applyStateChange(companyId, journey, target);
        Journey saved = journeyService.save(journey);
        return JourneyResponse.from(saved);
    }

    private Journey findAuthorizedJourney(Long companyId, Long journeyId) {
        if (isAdmin(companyId)) {
            return journeyService.findByCompanyIdAndJourneyId(companyId, journeyId);
        }
        if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.DRIVER)) {
            Optional<Driver> ownDriver = driverService.findByCompanyIdAndUserId(companyId,
                    currentUserProvider.get().userId());
            if (ownDriver.isEmpty()) {
                throw new ForbiddenOperationException(
                        "Tu usuario no tiene un conductor vinculado en esta empresa");
            }
            return journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(companyId, journeyId,
                            ownDriver.get().getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Viaje no encontrado con el id " + journeyId + " en la empresa " + companyId));
        }
        throw new ForbiddenOperationException(
                "Solo SUPER_ADMIN, SECRETARY o el DRIVER asignado pueden actualizar el estado del viaje");
    }

    private void enforceDriverRules(Long companyId, Journey journey, JourneyState target) {
        if (!DRIVER_ALLOWED_TARGETS.contains(target)) {
            throw new ForbiddenOperationException(
                    "Un DRIVER solo puede iniciar (IN_PROGRESS) o completar (COMPLETED) sus viajes");
        }
        if (journey.getExecutionType() != JourneyExecutionType.PARTNER) {
            throw new ForbiddenOperationException("Un DRIVER solo puede actualizar viajes PARTNER asignados");
        }
    }

    private void applyStateChange(Long companyId, Journey journey, JourneyState target) {
        journey.setJourneyState(target);
        if (target == JourneyState.IN_PROGRESS) {
            if (journey.getJourneyStartDate() == null) {
                journey.setJourneyStartDate(LocalDateTime.now());
            }
            if (journey.getExecutionType() == JourneyExecutionType.PARTNER) {
                requireAssignmentVigente(companyId, journey);
            }
        } else if (target == JourneyState.COMPLETED) {
            if (journey.getJourneyEndDate() == null) {
                journey.setJourneyEndDate(LocalDateTime.now());
            }
            if (journey.getJourneyStartDate() != null
                    && journey.getJourneyEndDate().isBefore(journey.getJourneyStartDate())) {
                throw new ConflictException("La fecha de fin no puede ser anterior a la fecha de inicio");
            }
        }
    }

    private void requireAssignmentVigente(Long companyId, Journey journey) {
        TruckDriverAssignment assignment =
                assignmentService.findByCompanyIdAndAssignmentId(companyId, journey.getAssignmentId());
        LocalDateTime now = LocalDateTime.now();
        if (assignment.getAssignedFrom().isAfter(now)
                || (assignment.getAssignedUntil() != null && !assignment.getAssignedUntil().isAfter(now))) {
            throw new ConflictException("La asignación " + assignment.getAssignmentId()
                    + " ya no está vigente; no se puede iniciar el viaje");
        }
    }

    private boolean isAdmin(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
