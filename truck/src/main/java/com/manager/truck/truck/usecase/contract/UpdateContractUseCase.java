package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContainerService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateContractUseCase {

    private final ContractService contractService;
    private final ContainerService containerService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContractResponse execute(Long contractId, ContractUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Contract contract = contractService.findById(contractId);
        if (request.getOperationType() != null) contract.setOperationType(request.getOperationType());
        if (request.getOriginLocation() != null) contract.setOriginLocation(request.getOriginLocation());
        if (request.getDestinationLocation() != null) contract.setDestinationLocation(request.getDestinationLocation());
        if (request.getExpectedStartDate() != null) contract.setExpectedStartDate(request.getExpectedStartDate());
        if (request.getExpectedDeliveryDate() != null) contract.setExpectedDeliveryDate(request.getExpectedDeliveryDate());
        if (request.getActualDeliveryDate() != null) contract.setActualDeliveryDate(request.getActualDeliveryDate());
        if (request.getReferenceDocument() != null) contract.setReferenceDocument(request.getReferenceDocument());
        if (request.getDeclaredTotalWeightKg() != null) contract.setDeclaredTotalWeightKg(request.getDeclaredTotalWeightKg());
        if (request.getExpectedContainerCount() != null) {
            long currentContainers = containerService.countActiveByContractId(contractId);
            if (request.getExpectedContainerCount() < currentContainers) {
                throw new ConflictException("La cantidad declarada no puede ser menor que los "
                        + currentContainers + " contenedores existentes");
            }
            contract.setExpectedContainerCount(request.getExpectedContainerCount());
        }
        if (request.getObservations() != null) contract.setObservations(request.getObservations());
        if (request.getContractAmount() != null) contract.setContractAmount(request.getContractAmount());
        if (request.getContractDate() != null) contract.setContractDate(request.getContractDate());
        if (request.getContractDescription() != null) contract.setContractDescription(request.getContractDescription());
        if (request.getContractType() != null) contract.setContractType(request.getContractType());
        Contract saved = contractService.update(contract);
        return ContractResponse.from(saved);
    }
}
