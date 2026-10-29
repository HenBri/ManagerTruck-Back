package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Container;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContainerRepository extends JpaRepository<Container, Long> {

    @Query("SELECT c FROM Container c WHERE c.containerId = :containerId AND c.isDelete = false")
    Optional<Container> findActiveById(@Param("containerId") Long containerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Container c WHERE c.containerId = :containerId AND c.isDelete = false")
    Optional<Container> findActiveByIdForUpdate(@Param("containerId") Long containerId);

    @Query("SELECT c FROM Container c WHERE c.isDelete = false AND NOT EXISTS " +
            "(SELECT f.freightId FROM Freight f WHERE f.containerId = c.containerId " +
            "AND f.isDelete = false AND f.state IN " +
            "(com.manager.truck.truck.domain.enums.FreightState.PENDIENTE, " +
            "com.manager.truck.truck.domain.enums.FreightState.EN_PROCESO))")
    Page<Container> findAllAvailableForFreight(Pageable pageable);

    @Query("SELECT c FROM Container c WHERE c.chargeId = :chargeId AND c.isDelete = false")
    Page<Container> findByChargeId(@Param("chargeId") Long chargeId, Pageable pageable);

    @Query("SELECT COUNT(c) FROM Container c JOIN Charge ch ON ch.chargeId = c.chargeId " +
            "WHERE ch.contractId = :contractId AND c.isDelete = false")
    long countActiveByContractId(@Param("contractId") Long contractId);

    @Query("SELECT c FROM Container c WHERE c.isDelete = false")
    Page<Container> findAllActive(Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Container c " +
            "WHERE c.containerCode = :containerCode AND c.isDelete = false")
    boolean existsByContainerCode(@Param("containerCode") String containerCode);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Container c " +
            "WHERE c.containerCode = :containerCode AND c.containerId <> :containerId AND c.isDelete = false")
    boolean existsByContainerCodeAndContainerIdNot(@Param("containerCode") String containerCode, @Param("containerId") Long containerId);
}
