package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.AuxiliaryProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuxiliaryProviderServiceImpl implements AuxiliaryProviderService {

    private final AuxiliaryProviderRepository auxiliaryProviderRepository;

    @Override
    public AuxiliaryProvider save(AuxiliaryProvider provider) {
        return auxiliaryProviderRepository.save(provider);
    }

    @Override
    public AuxiliaryProvider findByCompanyIdAndProviderId(Long companyId, Long providerId) {
        return auxiliaryProviderRepository.findByCompanyIdAndProviderId(companyId, providerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Proveedor de auxilio no encontrado con el id " + providerId + " en la empresa " + companyId));
    }

    @Override
    public Optional<AuxiliaryProvider> findOptionalByCompanyIdAndProviderId(Long companyId, Long providerId) {
        return auxiliaryProviderRepository.findByCompanyIdAndProviderId(companyId, providerId);
    }

    @Override
    public Page<AuxiliaryProvider> findByCompanyId(Long companyId, Pageable pageable) {
        return auxiliaryProviderRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<AuxiliaryProvider> findByCompanyIdAndProviderKind(Long companyId, AuxiliaryProviderKind kind,
                                                                  Pageable pageable) {
        return auxiliaryProviderRepository.findByCompanyIdAndProviderKind(companyId, kind, pageable);
    }

    @Override
    public Page<AuxiliaryProvider> findByCompanyIdAndProviderStatus(Long companyId, AuxiliaryProviderStatus status,
                                                                    Pageable pageable) {
        return auxiliaryProviderRepository.findByCompanyIdAndProviderStatus(companyId, status, pageable);
    }

    @Override
    public Page<AuxiliaryProvider> findByCompanyIdAndProviderKindAndProviderStatus(Long companyId,
                                                                                   AuxiliaryProviderKind kind,
                                                                                   AuxiliaryProviderStatus status,
                                                                                   Pageable pageable) {
        return auxiliaryProviderRepository.findByCompanyIdAndProviderKindAndProviderStatus(
                companyId, kind, status, pageable);
    }

    @Override
    public Page<AuxiliaryProvider> findByCompanyIdAndProviderNameContains(Long companyId, String name,
                                                                          Pageable pageable) {
        return auxiliaryProviderRepository.findByCompanyIdAndProviderNameContains(companyId, name, pageable);
    }

    @Override
    public boolean existsByCompanyIdAndProviderName(Long companyId, String providerName) {
        return auxiliaryProviderRepository.existsByCompanyIdAndProviderName(companyId, providerName);
    }

    @Override
    public boolean existsByCompanyIdAndProviderNameNot(Long companyId, String providerName, Long providerId) {
        return auxiliaryProviderRepository.existsByCompanyIdAndProviderNameNot(companyId, providerName, providerId);
    }
}
