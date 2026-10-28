package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.JourneyStatusRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateJourneyStatusUseCase {

    private static final Map<JourneyState, Set<JourneyState>> ALLOWED_TRANSITIONS = Map.of(
            JourneyState.PENDIENTE, Set.of(JourneyState.EN_PROCESO, JourneyState.CANCELADO),
            JourneyState.EN_PROCESO, Set.of(JourneyState.COMPLETADO, JourneyState.CANCELADO),
            JourneyState.COMPLETADO, Set.of(),
            JourneyState.CANCELADO, Set.of()
    );

    private final JourneyService journeyService;
    private final TruckDriverAssignmentService assignmentService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public JourneyResponse execute(Long journeyId, JourneyStatusRequest request) {
        Journey journey = journeyService.findById(journeyId);
        JourneyState current = journey.getState();
        JourneyState target = request.getState();

        if (current == target) {
            throw new ConflictException("El viaje ya se encuentra en el estado " + current);
        }
        Set<JourneyState> validTargets = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!validTargets.contains(target)) {
            throw new ConflictException("Transición de estado inválida para el viaje: " + current + " -> " + target);
        }

        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            if (currentUserProvider.isDriver()) {
                User user = userService.findById(currentUserProvider.get().userId());
                if (journey.getAssignmentId() == null) {
                    throw new ForbiddenOperationException("Solo el conductor asignado puede actualizar el estado");
                }
                TruckDriverAssignment assignment = assignmentService.findById(journey.getAssignmentId());
                if (!assignment.getDriverId().equals(user.getPersonId())) {
                    throw new ForbiddenOperationException("Solo el conductor asignado puede actualizar el estado de este viaje");
                }
            } else {
                throw new ForbiddenOperationException("No tienes permiso para cambiar el estado del viaje");
            }
        }

        journey.setState(target);
        if (target == JourneyState.EN_PROCESO && journey.getJourneyStartDate() == null) {
            journey.setJourneyStartDate(OffsetDateTime.now());
        } else if (target == JourneyState.COMPLETADO && journey.getJourneyEndDate() == null) {
            journey.setJourneyEndDate(OffsetDateTime.now());
        }

        Journey saved = journeyService.save(journey);
        return JourneyResponse.from(saved);
    }
}
