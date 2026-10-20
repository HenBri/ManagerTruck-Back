package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.dto.response.PartnersResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllPartnersUseCase {

    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public PartnersResponse execute(Long companyId, Pageable pageable) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Page<Partner> page;
        if (hasFullAccess(companyId)) {
            page = partnerService.findByCompanyId(companyId, pageable);
        } else {
            Long userId = currentUserProvider.get().userId();
            Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
            page = ownPartner
                    .map(partner -> (Page<Partner>) new PageImpl<>(List.of(partner), pageable, 1))
                    .orElseGet(() -> Page.empty(pageable));
        }
        Page<PartnerResponse> content = page.map(PartnerResponse::from);
        return new PartnersResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
