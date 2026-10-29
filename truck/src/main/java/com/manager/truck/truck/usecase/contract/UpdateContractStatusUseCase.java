package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractStatusRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContractService;
import com.manager.truck.truck.service.FreightService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class UpdateContractStatusUseCase {

    private final ContractService contractService;
    private final FreightService freightService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContractResponse execute(Long contractId, ContractStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Contract contract = contractService.findById(contractId);
        ContractState current = contract.getState();
        ContractState target = request.getState();
        if (current == target) {
            throw new ConflictException("El contrato ya se encuentra en el estado " + current);
        }
        contract.setState(target);
        if (target == ContractState.ENTREGADO && contract.getActualDeliveryDate() == null) {
            contract.setActualDeliveryDate(OffsetDateTime.now());
        }
        Contract saved = contractService.update(contract);
        if (target == ContractState.ENTREGADO) {
            freightService.completeByContractId(contractId);
        }
        return ContractResponse.from(saved);
    }
}
