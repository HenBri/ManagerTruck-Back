package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;

    @Override
    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    public Contract update(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    public Contract findById(Long contractId) {
        return contractRepository.findActiveById(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id " + contractId));
    }

    @Override
    public Contract findByIdForUpdate(Long contractId) {
        return contractRepository.findActiveByIdForUpdate(contractId)
                .orElseThrow(() -> new ResourceNotFoundException("Contrato no encontrado con id " + contractId));
    }

    @Override
    public Optional<Contract> findOptionalById(Long contractId) {
        return contractRepository.findActiveById(contractId);
    }

    @Override
    public boolean existsByContractCode(String contractCode) {
        return contractRepository.existsByContractCode(contractCode);
    }

    @Override
    public boolean existsByContractCodeAndContractIdNot(String contractCode, Long contractId) {
        return contractRepository.existsByContractCodeAndContractIdNot(contractCode, contractId);
    }

    @Override
    public Page<Contract> findAll(Pageable pageable) {
        return contractRepository.findAllActive(pageable);
    }

    @Override
    public Page<Contract> findByClientId(Long clientId, Pageable pageable) {
        return contractRepository.findByClientId(clientId, pageable);
    }

    @Override
    public Page<Contract> findAvailableForContainers(Pageable pageable) {
        return contractRepository.findAvailableForContainers(pageable);
    }

    @Override
    public Page<Contract> findAvailableForContainersByClientId(Long clientId, Pageable pageable) {
        return contractRepository.findAvailableForContainersByClientId(clientId, pageable);
    }
}
