package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssignTruckDriverUseCase {

    private final TruckDriverAssignmentService assignmentService;
    private final TruckService truckService;
    private final DriverService driverService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckDriverAssignmentResponse execute(TruckDriverAssignmentRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        if (request.getAssignedUntil() != null
                && !request.getAssignedUntil().isAfter(request.getAssignedFrom())) {
            throw new BadRequestException("assignedUntil debe ser posterior a assignedFrom");
        }
        Truck truck = truckService.findById(request.getTruckId());
        Driver driver = driverService.findById(request.getDriverId());
        if (!"ACTIVO".equalsIgnoreCase(truck.getState())) {
            throw new ConflictException("Solo se pueden asignar conductores a camiones activos; el camión "
                    + truck.getTruckId() + " está en estado " + truck.getState());
        }
        if (!"ACTIVO".equalsIgnoreCase(driver.getState())) {
            throw new ConflictException("No se pueden asignar conductores inactivos; el conductor "
                    + driver.getDriverId() + " está en estado " + driver.getState());
        }

        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setTruckId(truck.getTruckId());
        assignment.setDriverId(driver.getDriverId());
        assignment.setAssignedFrom(request.getAssignedFrom());
        assignment.setAssignedUntil(request.getAssignedUntil());
        assignment.setState("ACTIVE");
        TruckDriverAssignment saved = assignmentService.save(assignment);
        return TruckDriverAssignmentResponse.from(saved);
    }
}
