package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;
import com.manager.truck.truck.domain.enums.ContainerState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContainerService {

    Container save(Container container);

    Container findByCompanyIdAndContainerId(Long companyId, Long containerId);

    Optional<Container> findOptionalByCompanyIdAndContainerId(Long companyId, Long containerId);

    Page<Container> findByCompanyId(Long companyId, Pageable pageable);

    Page<Container> findByCompanyIdAndChargeId(Long companyId, Long chargeId, Pageable pageable);

    Page<Container> findByCompanyIdAndContainerState(Long companyId, ContainerState containerState,
                                                     Pageable pageable);

    Page<Container> findByCompanyIdAndChargeIdAndContainerState(Long companyId, Long chargeId,
                                                                ContainerState containerState, Pageable pageable);

    boolean existsByCompanyIdAndContainerCode(Long companyId, String containerCode);

    boolean existsByCompanyIdAndContainerCodeNot(Long companyId, String containerCode, Long containerId);
}
