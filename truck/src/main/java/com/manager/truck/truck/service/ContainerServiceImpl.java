package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.ContainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContainerServiceImpl implements ContainerService {

    private final ContainerRepository containerRepository;

    @Override
    public Container save(Container container) {
        return containerRepository.save(container);
    }

    @Override
    public Container update(Container container) {
        return containerRepository.save(container);
    }

    @Override
    public Container findById(Long containerId) {
        return containerRepository.findActiveById(containerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contenedor no encontrado con id " + containerId));
    }

    @Override
    public Optional<Container> findOptionalById(Long containerId) {
        return containerRepository.findActiveById(containerId);
    }

    @Override
    public Container findByIdForUpdate(Long containerId) {
        return containerRepository.findActiveByIdForUpdate(containerId)
                .orElseThrow(() -> new ResourceNotFoundException("Contenedor no encontrado con id " + containerId));
    }

    @Override
    public long countActiveByContractId(Long contractId) {
        return containerRepository.countActiveByContractId(contractId);
    }

    @Override
    public Page<Container> findAllAvailableForFreight(Pageable pageable) {
        return containerRepository.findAllAvailableForFreight(pageable);
    }

    @Override
    public boolean existsByContainerCode(String containerCode) {
        return containerRepository.existsByContainerCode(containerCode);
    }

    @Override
    public boolean existsByContainerCodeAndContainerIdNot(String containerCode, Long containerId) {
        return containerRepository.existsByContainerCodeAndContainerIdNot(containerCode, containerId);
    }

    @Override
    public Page<Container> findAll(Pageable pageable) {
        return containerRepository.findAllActive(pageable);
    }

    @Override
    public Page<Container> findByChargeId(Long chargeId, Pageable pageable) {
        return containerRepository.findByChargeId(chargeId, pageable);
    }
}
