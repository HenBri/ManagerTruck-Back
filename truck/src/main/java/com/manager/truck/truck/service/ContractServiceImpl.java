package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.enums.ContractState;
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
    public Contract findByCompanyIdAndContractId(Long companyId, Long contractId) {
        return contractRepository.findByCompanyIdAndContractId(companyId, contractId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Contrato no encontrado con el id " + contractId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Contract> findOptionalByCompanyIdAndContractId(Long companyId, Long contractId) {
        return contractRepository.findByCompanyIdAndContractId(companyId, contractId);
    }

    @Override
    public Page<Contract> findByCompanyId(Long companyId, Pageable pageable) {
        return contractRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<Contract> findByCompanyIdAndClientId(Long companyId, Long clientId, Pageable pageable) {
        return contractRepository.findByCompanyIdAndClientId(companyId, clientId, pageable);
    }

    @Override
    public Page<Contract> findByCompanyIdAndContractState(Long companyId, ContractState contractState,
                                                          Pageable pageable) {
        return contractRepository.findByCompanyIdAndContractState(companyId, contractState, pageable);
    }

    @Override
    public Page<Contract> findByCompanyIdAndClientIdAndContractState(Long companyId, Long clientId,
                                                                     ContractState contractState,
                                                                     Pageable pageable) {
        return contractRepository.findByCompanyIdAndClientIdAndContractState(companyId, clientId,
                contractState, pageable);
    }
}
