package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetAllContractUseCase {
    @Autowired
    private ContractService contractService;

    public ContractsResponse execute(){
        return new ContractsResponse(contractService.findAll());
    }
}
