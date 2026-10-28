package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface AuxiliaryProviderService {

    AuxiliaryProvider save(AuxiliaryProvider provider);

    AuxiliaryProvider update(AuxiliaryProvider provider);

    AuxiliaryProvider findById(Long providerId);

    Optional<AuxiliaryProvider> findOptionalById(Long providerId);

    boolean existsByProviderName(String providerName);

    boolean existsByProviderNameAndProviderIdNot(String providerName, Long providerId);

    Page<AuxiliaryProvider> findAll(Pageable pageable);

    Page<AuxiliaryProvider> findByProviderKind(AuxiliaryProviderKind kind, Pageable pageable);

    Page<AuxiliaryProvider> findByProviderNameContains(String name, Pageable pageable);
}
