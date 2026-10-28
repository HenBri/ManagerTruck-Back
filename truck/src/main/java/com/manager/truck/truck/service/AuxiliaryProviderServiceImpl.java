package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.AuxiliaryProvider;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
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

    private final AuxiliaryProviderRepository providerRepository;

    @Override
    public AuxiliaryProvider save(AuxiliaryProvider provider) {
        return providerRepository.save(provider);
    }

    @Override
    public AuxiliaryProvider update(AuxiliaryProvider provider) {
        return providerRepository.save(provider);
    }

    @Override
    public AuxiliaryProvider findById(Long providerId) {
        return providerRepository.findActiveById(providerId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado con id " + providerId));
    }

    @Override
    public Optional<AuxiliaryProvider> findOptionalById(Long providerId) {
        return providerRepository.findActiveById(providerId);
    }

    @Override
    public boolean existsByProviderName(String providerName) {
        return providerRepository.existsByProviderName(providerName);
    }

    @Override
    public boolean existsByProviderNameAndProviderIdNot(String providerName, Long providerId) {
        return providerRepository.existsByProviderNameAndProviderIdNot(providerName, providerId);
    }

    @Override
    public Page<AuxiliaryProvider> findAll(Pageable pageable) {
        return providerRepository.findAllActive(pageable);
    }

    @Override
    public Page<AuxiliaryProvider> findByProviderKind(AuxiliaryProviderKind kind, Pageable pageable) {
        return providerRepository.findByProviderKind(kind, pageable);
    }

    @Override
    public Page<AuxiliaryProvider> findByProviderNameContains(String name, Pageable pageable) {
        return providerRepository.findByProviderNameContains(name, pageable);
    }
}
