package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveContractUseCase {
    @Autowired
    private ContractService contractService;

    public ContractResponse execute(ContractRequest contractRequest){
        Contract contract = contractBuild(contractRequest);
        contractService.save(contract);
        return contractResponseBuild(contract);
    }
    private Contract contractBuild(ContractRequest contractRequest){
        Contract contract = new Contract();
        contract.setDescription(contractRequest.getDescription());
        contract.setType(contractRequest.getType());
        contract.setAmount(contractRequest.getAmount());
        return contract;
    }
    private ContractResponse contractResponseBuild(Contract contract){
        return new ContractResponse(contract);
    }
}
