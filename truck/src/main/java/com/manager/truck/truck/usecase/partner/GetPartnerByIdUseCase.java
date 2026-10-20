package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPartnerByIdUseCase {

    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public PartnerResponse execute(Long companyId, Long partnerId) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Partner partner = partnerService.findByCompanyIdAndPartnerId(companyId, partnerId);
        if (!hasFullAccess(companyId)) {
            Long userId = currentUserProvider.get().userId();
            if (!userId.equals(partner.getUserId())) {
                throw new ForbiddenOperationException("Solo puedes consultar tu propio perfil de socio");
            }
        }
        return PartnerResponse.from(partner);
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
