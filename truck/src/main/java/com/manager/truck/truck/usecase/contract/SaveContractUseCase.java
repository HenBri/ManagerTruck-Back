package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.OperationType;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveContractUseCase {

    private final ContractService contractService;
    private final ClientService clientService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContractResponse execute(ContractRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Client client = clientService.findById(request.getClientId());
        String code = request.getContractCode().trim();
        if (contractService.existsByContractCode(code)) {
            throw new ConflictException("Ya existe un contrato con el código " + code);
        }
        Contract contract = new Contract();
        contract.setClientId(client.getClientId());
        contract.setContractCode(code);
        contract.setOperationType(request.getOperationType() != null ? request.getOperationType() : OperationType.IMPORTACION);
        contract.setOriginLocation(request.getOriginLocation());
        contract.setDestinationLocation(request.getDestinationLocation());
        contract.setExpectedStartDate(request.getExpectedStartDate());
        contract.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        contract.setReferenceDocument(request.getReferenceDocument());
        contract.setDeclaredTotalWeightKg(request.getDeclaredTotalWeightKg());
        contract.setExpectedContainerCount(request.getExpectedContainerCount());
        contract.setObservations(request.getObservations());
        contract.setContractAmount(request.getContractAmount());
        contract.setContractDate(request.getContractDate());
        contract.setContractDescription(request.getContractDescription());
        contract.setContractType(request.getContractType());
        contract.setState(ContractState.PENDIENTE);
        Contract saved = contractService.save(contract);
        return ContractResponse.from(saved);
    }
}
