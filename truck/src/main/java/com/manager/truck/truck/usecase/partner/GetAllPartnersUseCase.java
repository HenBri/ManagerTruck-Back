package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.dto.response.PartnersResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllPartnersUseCase {

    private final PartnerService partnerService;
    private final CurrentUserProvider currentUserProvider;

    public PartnersResponse execute(Pageable pageable) {
        Page<Partner> page;
        if (currentUserProvider.isAdmin() || currentUserProvider.isSecretary()) {
            page = partnerService.findAll(pageable);
        } else {
            Long currentUserId = currentUserProvider.get().userId();
            page = partnerService.findOptionalById(currentUserId)
                    .map(partner -> (Page<Partner>) new PageImpl<>(List.of(partner), pageable, 1))
                    .orElseGet(() -> Page.empty(pageable));
        }
        Page<PartnerResponse> content = page.map(PartnerResponse::from);
        return new PartnersResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
