package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProvidersResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllAuxiliaryProvidersUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CurrentUserProvider currentUserProvider;

    public AuxiliaryProvidersResponse execute(AuxiliaryProviderKind kind, String name, Pageable pageable) {
        currentUserProvider.requireAdminOrSecretary();
        Page<AuxiliaryProvider> page;
        if (kind != null) {
            page = auxiliaryProviderService.findByProviderKind(kind, pageable);
        } else if (name != null && !name.isBlank()) {
            page = auxiliaryProviderService.findByProviderNameContains(name.trim(), pageable);
        } else {
            page = auxiliaryProviderService.findAll(pageable);
        }
        Page<AuxiliaryProviderResponse> content = page.map(AuxiliaryProviderResponse::from);
        return new AuxiliaryProvidersResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
