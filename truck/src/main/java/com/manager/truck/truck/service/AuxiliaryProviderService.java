package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuxiliaryProviderService {

    AuxiliaryProvider save(AuxiliaryProvider provider);

    AuxiliaryProvider findByCompanyIdAndProviderId(Long companyId, Long providerId);

    Optional<AuxiliaryProvider> findOptionalByCompanyIdAndProviderId(Long companyId, Long providerId);

    Page<AuxiliaryProvider> findByCompanyId(Long companyId, Pageable pageable);

    Page<AuxiliaryProvider> findByCompanyIdAndProviderKind(Long companyId, AuxiliaryProviderKind kind,
                                                           Pageable pageable);

    Page<AuxiliaryProvider> findByCompanyIdAndProviderStatus(Long companyId, AuxiliaryProviderStatus status,
                                                             Pageable pageable);

    Page<AuxiliaryProvider> findByCompanyIdAndProviderKindAndProviderStatus(Long companyId,
                                                                            AuxiliaryProviderKind kind,
                                                                            AuxiliaryProviderStatus status,
                                                                            Pageable pageable);

    Page<AuxiliaryProvider> findByCompanyIdAndProviderNameContains(Long companyId, String name,
                                                                   Pageable pageable);

    boolean existsByCompanyIdAndProviderName(Long companyId, String providerName);

    boolean existsByCompanyIdAndProviderNameNot(Long companyId, String providerName, Long providerId);
}
