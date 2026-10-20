package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateContractStatusUseCase {

    private static final Map<ContractState, Set<ContractState>> ALLOWED_TRANSITIONS = Map.of(
            ContractState.DRAFT, Set.of(ContractState.ACTIVE, ContractState.CANCELLED),
            ContractState.ACTIVE, Set.of(ContractState.COMPLETED, ContractState.CANCELLED),
            ContractState.COMPLETED, Set.of(),
            ContractState.CANCELLED, Set.of());

    private final ContractService contractService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContractResponse execute(Long companyId, Long contractId, ContractStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Contract contract = contractService.findByCompanyIdAndContractId(companyId, contractId);
        ContractState current = contract.getContractState();
        ContractState target = request.getStatus();
        if (current == target) {
            throw new ConflictException("El contrato ya se encuentra en el estado " + current);
        }
        if (!ALLOWED_TRANSITIONS.get(current).contains(target)) {
            throw new ConflictException(
                    "Transición de estado inválida para el contrato: " + current + " -> " + target);
        }
        contract.setContractState(target);
        Contract saved = contractService.save(contract);
        return ContractResponse.from(saved);
    }
}
