package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.enums.ContractState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ContractRepository extends JpaRepository<Contract, Long> {

    @Query("SELECT c FROM Contract c WHERE c.companyId = :companyId AND c.contractId = :contractId")
    Optional<Contract> findByCompanyIdAndContractId(@Param("companyId") Long companyId,
                                                    @Param("contractId") Long contractId);

    @Query("SELECT c FROM Contract c WHERE c.companyId = :companyId")
    Page<Contract> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.companyId = :companyId AND c.clientId = :clientId")
    Page<Contract> findByCompanyIdAndClientId(@Param("companyId") Long companyId,
                                              @Param("clientId") Long clientId,
                                              Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.companyId = :companyId AND c.contractState = :contractState")
    Page<Contract> findByCompanyIdAndContractState(@Param("companyId") Long companyId,
                                                   @Param("contractState") ContractState contractState,
                                                   Pageable pageable);

    @Query("SELECT c FROM Contract c WHERE c.companyId = :companyId AND c.clientId = :clientId "
            + "AND c.contractState = :contractState")
    Page<Contract> findByCompanyIdAndClientIdAndContractState(
            @Param("companyId") Long companyId,
            @Param("clientId") Long clientId,
            @Param("contractState") ContractState contractState,
            Pageable pageable);
}
