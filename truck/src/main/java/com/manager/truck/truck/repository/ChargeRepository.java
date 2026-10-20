package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.enums.ChargeState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChargeRepository extends JpaRepository<Charge, Long> {

    @Query("SELECT c FROM Charge c WHERE c.companyId = :companyId AND c.chargeId = :chargeId")
    Optional<Charge> findByCompanyIdAndChargeId(@Param("companyId") Long companyId,
                                                @Param("chargeId") Long chargeId);

    @Query("SELECT c FROM Charge c WHERE c.companyId = :companyId")
    Page<Charge> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.companyId = :companyId AND c.contractId = :contractId")
    Page<Charge> findByCompanyIdAndContractId(@Param("companyId") Long companyId,
                                              @Param("contractId") Long contractId,
                                              Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.companyId = :companyId AND c.chargeState = :chargeState")
    Page<Charge> findByCompanyIdAndChargeState(@Param("companyId") Long companyId,
                                               @Param("chargeState") ChargeState chargeState,
                                               Pageable pageable);

    @Query("SELECT c FROM Charge c WHERE c.companyId = :companyId AND c.contractId = :contractId "
            + "AND c.chargeState = :chargeState")
    Page<Charge> findByCompanyIdAndContractIdAndChargeState(
            @Param("companyId") Long companyId,
            @Param("contractId") Long contractId,
            @Param("chargeState") ChargeState chargeState,
            Pageable pageable);
}
