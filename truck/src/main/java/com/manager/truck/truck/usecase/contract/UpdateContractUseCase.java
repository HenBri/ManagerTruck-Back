package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateContractUseCase {
    @Autowired
    private ContractService contractService;

    public ContractResponse execute(Long id, ContractRequest contractRequest){
        Contract contract = contractBuild(id, contractRequest);
        contractService.save(contract);
        return contractResponseBuild(contract);
    }

    private ContractResponse contractResponseBuild(Contract contract) {
        return new ContractResponse(contract);
    }

    private Contract contractBuild(Long id, ContractRequest contractRequest){
        Contract contract = contractService.findById(id);
        contract.setDescription(contractRequest.getDescription());
        contract.setType(contractRequest.getType());
        contract.setAmount(contractRequest.getAmount());
        return contract;
    }
}
