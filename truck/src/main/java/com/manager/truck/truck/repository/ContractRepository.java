package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("SELECT c FROM Contract c WHERE c.contractId = :contractId AND c.isDelete = false")
    Optional<Contract> findActiveById(@Param("contractId") Long contractId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Contract c WHERE c.contractId = :contractId AND c.isDelete = false")
    Optional<Contract> findActiveByIdForUpdate(@Param("contractId") Long contractId);

    @Query("SELECT c FROM Contract c WHERE c.isDelete = false")
    Page<Contract> findAllActive(Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.clientId = :clientId AND c.isDelete = false")
    Page<Contract> findByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.isDelete = false AND " +
            "(c.expectedContainerCount IS NULL OR " +
            "(SELECT COUNT(co.containerId) FROM Container co JOIN Charge ch ON ch.chargeId = co.chargeId " +
            "WHERE ch.contractId = c.contractId AND co.isDelete = false) < c.expectedContainerCount)")
    Page<Contract> findAvailableForContainers(Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.clientId = :clientId AND c.isDelete = false AND " +
            "(c.expectedContainerCount IS NULL OR " +
            "(SELECT COUNT(co.containerId) FROM Container co JOIN Charge ch ON ch.chargeId = co.chargeId " +
            "WHERE ch.contractId = c.contractId AND co.isDelete = false) < c.expectedContainerCount)")
    Page<Contract> findAvailableForContainersByClientId(@Param("clientId") Long clientId, Pageable pageable);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c " +
            "WHERE c.contractCode = :contractCode AND c.isDelete = false")
    boolean existsByContractCode(@Param("contractCode") String contractCode);

    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contract c " +
            "WHERE c.contractCode = :contractCode AND c.contractId <> :contractId AND c.isDelete = false")
    boolean existsByContractCodeAndContractIdNot(@Param("contractCode") String contractCode, @Param("contractId") Long contractId);
}
