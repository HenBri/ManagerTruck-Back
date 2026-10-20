package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetTruckByIdUseCase {

    private final TruckService truckService;
    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public TruckResponse execute(Long companyId, Long truckId) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Truck truck = truckService.findByCompanyIdAndTruckId(companyId, truckId);
        if (!hasFullAccess(companyId)) {
            if (!currentUserProvider.hasCompanyRole(companyId, SecurityRoles.PARTNER)) {
                throw new ForbiddenOperationException("No tienes acceso a los camiones de esta empresa");
            }
            Long userId = currentUserProvider.get().userId();
            Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
            if (ownPartner.isEmpty() || !ownPartner.get().getPartnerId().equals(truck.getOwnerPartnerId())) {
                throw new ForbiddenOperationException("Solo puedes consultar tus propios camiones");
            }
        }
        return TruckResponse.from(truck);
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
