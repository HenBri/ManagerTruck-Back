package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.request.PartnerStatusRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePartnerStatusUseCase {

    private final PartnerService partnerService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PartnerResponse execute(Long partnerId, PartnerStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Partner partner = partnerService.findById(partnerId);
        String current = partner.getState();
        String target = request.getState();
        if (target.equalsIgnoreCase(current)) {
            throw new ConflictException("El socio ya se encuentra en el estado " + current);
        }
        partner.setState(target);
        Partner saved = partnerService.update(partner);
        return PartnerResponse.from(saved);
    }
}
