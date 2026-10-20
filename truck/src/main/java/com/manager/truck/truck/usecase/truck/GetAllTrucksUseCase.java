package com.manager.truck.truck.usecase.truck;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Truck;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.TruckService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllTrucksUseCase {

    private final TruckService truckService;
    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public TrucksResponse execute(Long companyId, Pageable pageable) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Page<Truck> page;
        if (hasFullAccess(companyId)) {
            page = truckService.findByCompanyId(companyId, pageable);
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.PARTNER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
            page = ownPartner
                    .map(partner -> truckService.findByCompanyIdAndOwnerPartnerId(
                            companyId, partner.getPartnerId(), pageable))
                    .orElseGet(() -> Page.empty(pageable));
        } else {
            throw new ForbiddenOperationException("No tienes acceso a los camiones de esta empresa");
        }
        Page<TruckResponse> content = page.map(TruckResponse::from);
        return new TrucksResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
