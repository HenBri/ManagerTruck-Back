package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProvidersResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllAuxiliaryProvidersUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public AuxiliaryProvidersResponse execute(Long companyId, AuxiliaryProviderKind kind,
                                              AuxiliaryProviderStatus status, String name,
                                              Pageable pageable) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Page<AuxiliaryProvider> page;
        if (kind != null && status != null) {
            page = auxiliaryProviderService.findByCompanyIdAndProviderKindAndProviderStatus(
                    companyId, kind, status, pageable);
        } else if (kind != null) {
            page = auxiliaryProviderService.findByCompanyIdAndProviderKind(companyId, kind, pageable);
        } else if (status != null) {
            page = auxiliaryProviderService.findByCompanyIdAndProviderStatus(companyId, status, pageable);
        } else if (name != null && !name.isBlank()) {
            page = auxiliaryProviderService.findByCompanyIdAndProviderNameContains(companyId, name.trim(),
                    pageable);
        } else {
            page = auxiliaryProviderService.findByCompanyId(companyId, pageable);
        }
        Page<AuxiliaryProviderResponse> content = page.map(AuxiliaryProviderResponse::from);
        return new AuxiliaryProvidersResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
