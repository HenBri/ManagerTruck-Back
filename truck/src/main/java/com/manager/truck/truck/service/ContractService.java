package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.enums.ContractState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContractService {

    Contract save(Contract contract);

    Contract findByCompanyIdAndContractId(Long companyId, Long contractId);

    Optional<Contract> findOptionalByCompanyIdAndContractId(Long companyId, Long contractId);

    Page<Contract> findByCompanyId(Long companyId, Pageable pageable);

    Page<Contract> findByCompanyIdAndClientId(Long companyId, Long clientId, Pageable pageable);

    Page<Contract> findByCompanyIdAndContractState(Long companyId, ContractState contractState, Pageable pageable);

    Page<Contract> findByCompanyIdAndClientIdAndContractState(Long companyId, Long clientId,
                                                              ContractState contractState, Pageable pageable);
}
