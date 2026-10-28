package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderUpdateRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAuxiliaryProviderUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AuxiliaryProviderResponse execute(Long providerId, AuxiliaryProviderUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        AuxiliaryProvider provider = auxiliaryProviderService.findById(providerId);
        String name = request.getProviderName().trim();
        if (!name.equalsIgnoreCase(provider.getProviderName())
                && auxiliaryProviderService.existsByProviderNameAndProviderIdNot(name, providerId)) {
            throw new ConflictException("Ya existe un proveedor de auxilio con el nombre " + name);
        }
        provider.setProviderKind(request.getProviderKind());
        provider.setProviderName(name);
        provider.setProviderDocument(trimToNull(request.getProviderDocument()));
        provider.setProviderPhone(trimToNull(request.getProviderPhone()));
        provider.setProviderEmail(trimToNull(request.getProviderEmail()));
        AuxiliaryProvider saved = auxiliaryProviderService.update(provider);
        return AuxiliaryProviderResponse.from(saved);
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
