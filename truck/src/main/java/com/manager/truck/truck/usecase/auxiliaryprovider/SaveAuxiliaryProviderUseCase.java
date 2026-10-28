package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveAuxiliaryProviderUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AuxiliaryProviderResponse execute(AuxiliaryProviderRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        String name = request.getProviderName().trim();
        if (auxiliaryProviderService.existsByProviderName(name)) {
            throw new ConflictException("Ya existe un proveedor de auxilio con el nombre " + name);
        }

        AuxiliaryProvider provider = new AuxiliaryProvider();
        provider.setProviderKind(request.getProviderKind());
        provider.setProviderName(name);
        provider.setProviderDocument(trimToNull(request.getProviderDocument()));
        provider.setProviderPhone(trimToNull(request.getProviderPhone()));
        provider.setProviderEmail(trimToNull(request.getProviderEmail()));
        provider.setState("ACTIVE");
        AuxiliaryProvider saved = auxiliaryProviderService.save(provider);
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
