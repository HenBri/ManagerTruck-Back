package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveContractUseCase {

    private final ContractService contractService;
    private final ClientService clientService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContractResponse execute(Long companyId, ContractRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear contratos en una empresa inactiva");
        }
        Client client = clientService.findByCompanyIdAndClientId(companyId, request.getClientId());
        if (!Boolean.TRUE.equals(client.getClientState())) {
            throw new ConflictException("No se pueden crear contratos para un cliente inactivo");
        }
        Contract contract = new Contract();
        contract.setCompanyId(companyId);
        contract.setClientId(client.getClientId());
        contract.setContractAmount(request.getContractAmount());
        contract.setContractDate(request.getContractDate());
        contract.setContractDescription(request.getContractDescription());
        contract.setContractType(request.getContractType());
        contract.setContractState(ContractState.DRAFT);
        Contract saved = contractService.save(contract);
        return ContractResponse.from(saved);
    }
}
