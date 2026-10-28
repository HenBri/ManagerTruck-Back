package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetTruckDriverAssignmentByIdUseCase {

    private final TruckDriverAssignmentService assignmentService;
    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public TruckDriverAssignmentResponse execute(Long assignmentId) {
        TruckDriverAssignment assignment = assignmentService.findById(assignmentId);
        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            User user = userService.findById(currentUserProvider.get().userId());
            if (currentUserProvider.isDriver()) {
                if (user.getPersonId() == null || !user.getPersonId().equals(assignment.getDriverId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar tus propias asignaciones");
                }
            } else if (currentUserProvider.isPartner()) {
                Truck truck = truckService.findById(assignment.getTruckId());
                if (user.getPersonId() == null || !user.getPersonId().equals(truck.getOwnerPartnerId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar asignaciones de tus propios camiones");
                }
            } else {
                throw new ForbiddenOperationException("No tienes permiso para consultar asignaciones");
            }
        }
        return TruckDriverAssignmentResponse.from(assignment);
    }
}
