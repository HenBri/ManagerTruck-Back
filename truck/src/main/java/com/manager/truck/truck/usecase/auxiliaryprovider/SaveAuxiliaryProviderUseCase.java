package com.manager.truck.truck.usecase.auxiliaryprovider;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.AuxiliaryProviderService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveAuxiliaryProviderUseCase {

    private final AuxiliaryProviderService auxiliaryProviderService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public AuxiliaryProviderResponse execute(Long companyId, AuxiliaryProviderRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear proveedores de auxilio en una empresa inactiva");
        }
        String name = request.getProviderName().trim();
        if (auxiliaryProviderService.existsByCompanyIdAndProviderName(companyId, name)) {
            throw new ConflictException("Ya existe un proveedor de auxilio con el nombre " + name + " en esta empresa");
        }
        AuxiliaryProvider provider = new AuxiliaryProvider();
        provider.setCompanyId(companyId);
        provider.setProviderKind(request.getProviderKind());
        provider.setProviderName(name);
        provider.setProviderDocument(trimToNull(request.getProviderDocument()));
        provider.setProviderPhone(trimToNull(request.getProviderPhone()));
        provider.setProviderEmail(trimToNull(request.getProviderEmail()));
        provider.setProviderStatus(AuxiliaryProviderStatus.ACTIVE);
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
