package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContainerService {

    Container save(Container container);

    Container update(Container container);

    Container findById(Long containerId);

    Optional<Container> findOptionalById(Long containerId);

    boolean existsByContainerCode(String containerCode);

    boolean existsByContainerCodeAndContainerIdNot(String containerCode, Long containerId);

    Page<Container> findAll(Pageable pageable);

    Page<Container> findByChargeId(Long chargeId, Pageable pageable);
}
