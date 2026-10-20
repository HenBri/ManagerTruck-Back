package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderUpdateRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateAuxiliaryProviderUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AuxiliaryProviderResponse execute(Long companyId, Long providerId,
                                             AuxiliaryProviderUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        AuxiliaryProvider provider = auxiliaryProviderService.findByCompanyIdAndProviderId(companyId, providerId);
        String name = request.getProviderName().trim();
        if (!name.equals(provider.getProviderName())
                && auxiliaryProviderService.existsByCompanyIdAndProviderNameNot(companyId, name, providerId)) {
            throw new ConflictException("Ya existe un proveedor de auxilio con el nombre " + name + " en esta empresa");
        }
        provider.setProviderKind(request.getProviderKind());
        provider.setProviderName(name);
        provider.setProviderDocument(trimToNull(request.getProviderDocument()));
        provider.setProviderPhone(trimToNull(request.getProviderPhone()));
        provider.setProviderEmail(trimToNull(request.getProviderEmail()));
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
