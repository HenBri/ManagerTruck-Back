package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyAccountProvisioningService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavePartnerUseCase {

    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CompanyAccountProvisioningService accountProvisioningService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PartnerResponse execute(Long companyId, PartnerRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear socios en una empresa inactiva");
        }
        User user = request.getPartnerType() == PartnerType.PERSON
                ? accountProvisioningService.provision(companyId, request.getEmail(), request.getPassword(),
                        request.getName(), request.getLastname(), request.getCi(), request.getPhone(), "PARTNER")
                : null;
        if (request.getPartnerType() == PartnerType.BUSINESS && isBlank(request.getPartnerName())) {
            throw new BadRequestException("partnerName es obligatorio para un socio de tipo BUSINESS");
        }
        Partner partner = new Partner();
        partner.setCompanyId(companyId);
        partner.setUserId(user == null ? null : user.getUserId());
        partner.setPartnerType(request.getPartnerType());
        partner.setPartnerName(user == null ? request.getPartnerName().trim()
                : user.getUserName() + " " + user.getUserLastname());
        partner.setPartnerDocument(user == null ? request.getPartnerDocument() : user.getUserCi());
        partner.setPartnerPhone(user == null ? request.getPartnerPhone() : user.getUserPhone());
        partner.setPartnerState(PartnerStatus.ACTIVE);
        Partner saved = partnerService.save(partner);
        return PartnerResponse.from(saved);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
