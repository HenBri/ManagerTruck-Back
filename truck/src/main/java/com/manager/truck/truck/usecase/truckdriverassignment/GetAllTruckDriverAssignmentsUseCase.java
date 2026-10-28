package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentsResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.TruckService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllTruckDriverAssignmentsUseCase {

    private final TruckDriverAssignmentService assignmentService;
    private final TruckService truckService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public TruckDriverAssignmentsResponse execute(Long truckId, Long driverId, Pageable pageable) {
        Page<TruckDriverAssignment> page;
        if (currentUserProvider.isAdmin() || currentUserProvider.isSecretary()) {
            if (truckId != null) {
                page = assignmentService.findByTruckId(truckId, pageable);
            } else if (driverId != null) {
                page = assignmentService.findByDriverId(driverId, pageable);
            } else {
                page = assignmentService.findAll(pageable);
            }
        } else if (currentUserProvider.isDriver()) {
            User user = userService.findById(currentUserProvider.get().userId());
            page = assignmentService.findByDriverId(user.getPersonId(), pageable);
        } else if (currentUserProvider.isPartner()) {
            User user = userService.findById(currentUserProvider.get().userId());
            if (truckId != null) {
                Truck truck = truckService.findById(truckId);
                if (!user.getPersonId().equals(truck.getOwnerPartnerId())) {
                    throw new ForbiddenOperationException("Solo puedes consultar asignaciones de tus propios camiones");
                }
                page = assignmentService.findByTruckId(truckId, pageable);
            } else {
                throw new ForbiddenOperationException("Debe especificar el truckId de un camión propio");
            }
        } else {
            throw new ForbiddenOperationException("No tienes permiso para consultar asignaciones");
        }
        Page<TruckDriverAssignmentResponse> content = page.map(TruckDriverAssignmentResponse::from);
        return new TruckDriverAssignmentsResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
