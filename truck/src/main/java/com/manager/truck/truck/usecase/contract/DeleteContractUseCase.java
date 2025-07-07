package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteContractUseCase {
    @Autowired
    private ContractService contractService;

    public void execute(Long id){
        contractService.save(contractBuild(id));
    }

    private Contract contractBuild(Long id){
        Contract contract = contractService.findById(id);
        contract.setState(false);
        return contract;
    }
}
