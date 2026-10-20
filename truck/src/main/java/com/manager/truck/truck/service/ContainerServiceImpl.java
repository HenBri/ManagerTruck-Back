package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.enums.ContainerState;
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
    public Container findByCompanyIdAndContainerId(Long companyId, Long containerId) {
        return containerRepository.findByCompanyIdAndContainerId(companyId, containerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contenedor no encontrado con el id " + containerId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Container> findOptionalByCompanyIdAndContainerId(Long companyId, Long containerId) {
        return containerRepository.findByCompanyIdAndContainerId(companyId, containerId);
    }

    @Override
    public Page<Container> findByCompanyId(Long companyId, Pageable pageable) {
        return containerRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<Container> findByCompanyIdAndChargeId(Long companyId, Long chargeId, Pageable pageable) {
        return containerRepository.findByCompanyIdAndChargeId(companyId, chargeId, pageable);
    }

    @Override
    public Page<Container> findByCompanyIdAndContainerState(Long companyId, ContainerState containerState,
                                                            Pageable pageable) {
        return containerRepository.findByCompanyIdAndContainerState(companyId, containerState, pageable);
    }

    @Override
    public Page<Container> findByCompanyIdAndChargeIdAndContainerState(Long companyId, Long chargeId,
                                                                       ContainerState containerState,
                                                                       Pageable pageable) {
        return containerRepository.findByCompanyIdAndChargeIdAndContainerState(companyId, chargeId,
                containerState, pageable);
    }

    @Override
    public boolean existsByCompanyIdAndContainerCode(Long companyId, String containerCode) {
        return containerRepository.existsByCompanyIdAndContainerCode(companyId, containerCode);
    }

    @Override
    public boolean existsByCompanyIdAndContainerCodeNot(Long companyId, String containerCode, Long containerId) {
        return containerRepository.existsByCompanyIdAndContainerCodeNot(companyId, containerCode, containerId);
    }
}
