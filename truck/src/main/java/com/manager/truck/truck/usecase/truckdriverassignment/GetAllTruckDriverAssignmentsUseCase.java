package com.manager.truck.truck.usecase.truckdriverassignment;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentsResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllTruckDriverAssignmentsUseCase {

    private final TruckDriverAssignmentService assignmentService;
    private final PartnerService partnerService;
    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public TruckDriverAssignmentsResponse execute(Long companyId, Long truckId, Long driverId,
                                                  Pageable pageable) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Page<TruckDriverAssignment> page;
        if (hasFullAccess(companyId)) {
            page = findWithOptionalFilters(companyId, truckId, driverId, pageable);
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.PARTNER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
            page = ownPartner
                    .map(partner -> findForPartner(companyId, partner.getPartnerId(), truckId, driverId, pageable))
                    .orElseGet(() -> Page.empty(pageable));
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.DRIVER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Driver> ownDriver = driverService.findByCompanyIdAndUserId(companyId, userId);
            page = ownDriver
                    .map(driver -> findForDriver(companyId, driver.getDriverId(), truckId, pageable))
                    .orElseGet(() -> Page.empty(pageable));
        } else {
            throw new ForbiddenOperationException("No tienes acceso a las asignaciones de esta empresa");
        }
        Page<TruckDriverAssignmentResponse> content = page.map(TruckDriverAssignmentResponse::from);
        return new TruckDriverAssignmentsResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }

    private Page<TruckDriverAssignment> findWithOptionalFilters(Long companyId, Long truckId, Long driverId,
                                                                Pageable pageable) {
        if (truckId != null && driverId != null) {
            return assignmentService.findByCompanyIdAndTruckIdAndDriverId(companyId, truckId, driverId, pageable);
        }
        if (truckId != null) {
            return assignmentService.findByCompanyIdAndTruckId(companyId, truckId, pageable);
        }
        if (driverId != null) {
            return assignmentService.findByCompanyIdAndDriverId(companyId, driverId, pageable);
        }
        return assignmentService.findByCompanyId(companyId, pageable);
    }

    private Page<TruckDriverAssignment> findForPartner(Long companyId, Long ownerPartnerId, Long truckId,
                                                       Long driverId, Pageable pageable) {
        if (truckId != null) {
            return assignmentService.findByCompanyIdAndOwnerPartnerIdAndTruckId(
                    companyId, ownerPartnerId, truckId, pageable);
        }
        if (driverId != null) {
            return assignmentService.findByCompanyIdAndOwnerPartnerIdAndDriverId(
                    companyId, ownerPartnerId, driverId, pageable);
        }
        return assignmentService.findByCompanyIdAndOwnerPartnerId(companyId, ownerPartnerId, pageable);
    }

    private Page<TruckDriverAssignment> findForDriver(Long companyId, Long ownDriverId, Long truckId,
                                                      Pageable pageable) {
        if (truckId != null) {
            return assignmentService.findByCompanyIdAndTruckIdAndDriverId(
                    companyId, truckId, ownDriverId, pageable);
        }
        return assignmentService.findByCompanyIdAndDriverId(companyId, ownDriverId, pageable);
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
