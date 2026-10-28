package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderStatusRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAuxiliaryProviderStatusUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AuxiliaryProviderResponse execute(Long providerId, AuxiliaryProviderStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        AuxiliaryProvider provider = auxiliaryProviderService.findById(providerId);
        String target = request.getState();
        if (target.equalsIgnoreCase(provider.getState())) {
            throw new ConflictException("El proveedor de auxilio ya se encuentra en el estado " + target);
        }
        provider.setState(target);
        AuxiliaryProvider saved = auxiliaryProviderService.update(provider);
        return AuxiliaryProviderResponse.from(saved);
    }
}
