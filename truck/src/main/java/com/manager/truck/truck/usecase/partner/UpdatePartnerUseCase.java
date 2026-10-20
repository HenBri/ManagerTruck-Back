package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.request.PartnerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePartnerUseCase {

    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PartnerResponse execute(Long companyId, Long partnerId, PartnerUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Partner partner = partnerService.findByCompanyIdAndPartnerId(companyId, partnerId);
        partner.setPartnerName(request.getPartnerName().trim());
        partner.setPartnerDocument(request.getPartnerDocument());
        partner.setPartnerPhone(request.getPartnerPhone());
        Partner saved = partnerService.save(partner);
        return PartnerResponse.from(saved);
    }
}
