package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Charge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {

    @Query("SELECT c FROM Charge c WHERE c.chargeId = :chargeId AND c.isDelete = false")
    Optional<Charge> findActiveById(@Param("chargeId") Long chargeId);

    @Query("SELECT c FROM Charge c WHERE c.contractId = :contractId AND c.isDelete = false")
    Page<Charge> findByContractId(@Param("contractId") Long contractId, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.isDelete = false")
    Page<Charge> findAllActive(Pageable pageable);
}
