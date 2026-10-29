package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Contract;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ContractService {

    Contract save(Contract contract);

    Contract update(Contract contract);

    Contract findById(Long contractId);

    Contract findByIdForUpdate(Long contractId);

    Optional<Contract> findOptionalById(Long contractId);

    boolean existsByContractCode(String contractCode);

    boolean existsByContractCodeAndContractIdNot(String contractCode, Long contractId);

    Page<Contract> findAll(Pageable pageable);

    Page<Contract> findByClientId(Long clientId, Pageable pageable);

    Page<Contract> findAvailableForContainers(Pageable pageable);

    Page<Contract> findAvailableForContainersByClientId(Long clientId, Pageable pageable);
}
