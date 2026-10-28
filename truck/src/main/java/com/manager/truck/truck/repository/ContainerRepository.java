package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    @Query("SELECT c FROM Container c WHERE c.containerId = :containerId AND c.isDelete = false")
    Optional<Container> findActiveById(@Param("containerId") Long containerId);

    @Query("SELECT c FROM Container c WHERE c.chargeId = :chargeId AND c.isDelete = false")
    Page<Container> findByChargeId(@Param("chargeId") Long chargeId, Pageable pageable);

    @Query("SELECT c FROM Container c WHERE c.isDelete = false")
    Page<Container> findAllActive(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Container c " +
            "WHERE c.containerCode = :containerCode AND c.isDelete = false")
    boolean existsByContainerCode(@Param("containerCode") String containerCode);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Container c " +
            "WHERE c.containerCode = :containerCode AND c.containerId <> :containerId AND c.isDelete = false")
    boolean existsByContainerCodeAndContainerIdNot(@Param("containerCode") String containerCode, @Param("containerId") Long containerId);
}
