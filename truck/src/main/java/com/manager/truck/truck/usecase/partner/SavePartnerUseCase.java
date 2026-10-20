package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavePartnerUseCase {

    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CompanyUserService companyUserService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PartnerResponse execute(Long companyId, PartnerRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear socios en una empresa inactiva");
        }
        if (request.getUserId() != null) {
            validateUserLink(companyId, request.getUserId());
            if (partnerService.existsByCompanyIdAndUserId(companyId, request.getUserId())) {
                throw new ConflictException(
                        "La cuenta ya está vinculada como socio de esta empresa; no se permiten cuentas duplicadas");
            }
        }
        Partner partner = new Partner();
        partner.setCompanyId(companyId);
        partner.setUserId(request.getUserId());
        partner.setPartnerType(request.getPartnerType());
        partner.setPartnerName(request.getPartnerName().trim());
        partner.setPartnerDocument(request.getPartnerDocument());
        partner.setPartnerPhone(request.getPartnerPhone());
        partner.setPartnerState(PartnerStatus.ACTIVE);
        Partner saved = partnerService.save(partner);
        return PartnerResponse.from(saved);
    }

    private void validateUserLink(Long companyId, Long userId) {
        CompanyUser membership = companyUserService.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La cuenta no es miembro de esta empresa"));
        if (membership.getMembershipStatus() != MembershipStatus.ACTIVE) {
            throw new ConflictException("No se puede vincular una cuenta con membresía inactiva");
        }
        User user = userService.findById(userId);
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new ConflictException("No se puede vincular una cuenta inactiva");
        }
    }
}
