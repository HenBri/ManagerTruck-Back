package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetPartnerByIdUseCase {

    private final PartnerService partnerService;
    private final CurrentUserProvider currentUserProvider;

    public PartnerResponse execute(Long partnerId) {
        Partner partner = partnerService.findById(partnerId);
        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            Long currentUserId = currentUserProvider.get().userId();
            if (!currentUserId.equals(partner.getPartnerId())) {
                throw new ForbiddenOperationException("Solo puedes consultar tu propio perfil de socio");
            }
        }
        return PartnerResponse.from(partner);
    }
}
