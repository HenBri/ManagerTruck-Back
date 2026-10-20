package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetAuxiliaryProviderByIdUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public AuxiliaryProviderResponse execute(Long companyId, Long providerId) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        AuxiliaryProvider provider = auxiliaryProviderService.findByCompanyIdAndProviderId(companyId, providerId);
        return AuxiliaryProviderResponse.from(provider);
    }
}
