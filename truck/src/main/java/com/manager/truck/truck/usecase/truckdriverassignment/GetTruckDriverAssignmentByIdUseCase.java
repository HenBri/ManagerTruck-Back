package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetTruckDriverAssignmentByIdUseCase {

    private final TruckDriverAssignmentService assignmentService;
    private final PartnerService partnerService;
    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public TruckDriverAssignmentResponse execute(Long companyId, Long assignmentId) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        TruckDriverAssignment assignment = assignmentService.findByCompanyIdAndAssignmentId(companyId, assignmentId);
        if (!hasFullAccess(companyId)) {
            if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.PARTNER)) {
                requireOwnTruck(companyId, assignment);
            } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.DRIVER)) {
                requireOwnAssignment(companyId, assignment);
            } else {
                throw new ForbiddenOperationException("No tienes acceso a las asignaciones de esta empresa");
            }
        }
        return TruckDriverAssignmentResponse.from(assignment);
    }

    private void requireOwnTruck(Long companyId, TruckDriverAssignment assignment) {
        Long userId = currentUserProvider.get().userId();
        Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
        Optional<Truck> truck = assignmentService.findTruckByCompanyIdAndTruckId(companyId, assignment.getTruckId());
        if (ownPartner.isEmpty() || truck.isEmpty()
                || !ownPartner.get().getPartnerId().equals(truck.get().getOwnerPartnerId())) {
            throw new ForbiddenOperationException("Solo puedes consultar asignaciones de tus propios camiones");
        }
    }

    private void requireOwnAssignment(Long companyId, TruckDriverAssignment assignment) {
        Long userId = currentUserProvider.get().userId();
        Optional<Driver> ownDriver = driverService.findByCompanyIdAndUserId(companyId, userId);
        if (ownDriver.isEmpty() || !ownDriver.get().getDriverId().equals(assignment.getDriverId())) {
            throw new ForbiddenOperationException("Solo puedes consultar tus propias asignaciones");
        }
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
