package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.TruckStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AssignTruckDriverUseCase {

    private static final LocalDateTime OPEN_ENDED_UNTIL = LocalDateTime.of(9999, 12, 31, 23, 59, 59);

    private final TruckDriverAssignmentService assignmentService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public TruckDriverAssignmentResponse execute(Long companyId, TruckDriverAssignmentRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear asignaciones en una empresa inactiva");
        }
        if (request.getAssignedUntil() != null
                && !request.getAssignedUntil().isAfter(request.getAssignedFrom())) {
            throw new BadRequestException("assignedUntil debe ser posterior a assignedFrom");
        }
        Truck truck = assignmentService.findTruckForUpdate(companyId, request.getTruckId());
        Driver driver = assignmentService.findDriverForUpdate(companyId, request.getDriverId());
        if (truck.getTruckState() != TruckStatus.ACTIVE) {
            throw new ConflictException("Solo se pueden asignar conductores a camiones activos; el camión "
                    + truck.getTruckId() + " está en estado " + truck.getTruckState());
        }
        if (driver.getDriverState() != DriverStatus.ACTIVE) {
            throw new ConflictException("No se pueden asignar conductores inactivos; el conductor "
                    + driver.getDriverId() + " está en estado " + driver.getDriverState());
        }
        LocalDateTime until = request.getAssignedUntil() != null ? request.getAssignedUntil() : OPEN_ENDED_UNTIL;
        if (assignmentService.existsOverlappingForTruck(
                companyId, truck.getTruckId(), request.getAssignedFrom(), until)) {
            throw new ConflictException("El camión " + truck.getTruckId()
                    + " ya tiene una asignación vigente incompatible con esas fechas");
        }
        if (assignmentService.existsOverlappingForDriver(
                companyId, driver.getDriverId(), request.getAssignedFrom(), until)) {
            throw new ConflictException("El conductor " + driver.getDriverId()
                    + " ya tiene una asignación vigente incompatible con esas fechas");
        }
        TruckDriverAssignment assignment = new TruckDriverAssignment();
        assignment.setCompanyId(companyId);
        assignment.setTruckId(truck.getTruckId());
        assignment.setDriverId(driver.getDriverId());
        assignment.setAssignedFrom(request.getAssignedFrom());
        assignment.setAssignedUntil(request.getAssignedUntil());
        TruckDriverAssignment saved = assignmentService.save(assignment);
        return TruckDriverAssignmentResponse.from(saved);
    }
}
