package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderStatusRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAuxiliaryProviderStatusUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AuxiliaryProviderResponse execute(Long companyId, Long providerId,
                                             AuxiliaryProviderStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        AuxiliaryProvider provider = auxiliaryProviderService.findByCompanyIdAndProviderId(companyId, providerId);
        AuxiliaryProviderStatus target = request.getStatus();
        if (provider.getProviderStatus() == target) {
            throw new ConflictException("El proveedor de auxilio ya se encuentra en el estado " + target);
        }
        provider.setProviderStatus(target);
        AuxiliaryProvider saved = auxiliaryProviderService.save(provider);
        return AuxiliaryProviderResponse.from(saved);
    }
}
