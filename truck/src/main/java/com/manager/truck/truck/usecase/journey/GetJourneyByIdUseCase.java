package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetJourneyByIdUseCase {

    private final JourneyService journeyService;
    private final TruckDriverAssignmentService assignmentService;
    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public JourneyResponse execute(Long journeyId) {
        Journey journey = journeyService.findById(journeyId);
        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            User user = userService.findById(currentUserProvider.get().userId());
            if (currentUserProvider.isDriver()) {
                if (journey.getAssignmentId() == null) {
                    throw new ForbiddenOperationException("No tienes acceso a este viaje");
                }
                TruckDriverAssignment assignment = assignmentService.findById(journey.getAssignmentId());
                if (!assignment.getDriverId().equals(user.getPersonId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar tus propios viajes");
                }
            } else if (currentUserProvider.isPartner()) {
                if (journey.getAssignmentId() == null) {
                    throw new ForbiddenOperationException("No tienes acceso a este viaje");
                }
                TruckDriverAssignment assignment = assignmentService.findById(journey.getAssignmentId());
                Truck truck = truckService.findById(assignment.getTruckId());
                if (!truck.getOwnerPartnerId().equals(user.getPersonId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar viajes de tus propios camiones");
                }
            } else {
                throw new ForbiddenOperationException("No tienes permiso para consultar este viaje");
            }
        }
        return JourneyResponse.from(journey);
    }
}
