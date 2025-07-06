package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetContractByIdUseCase {
    @Autowired
    private ContractService contractService;

    public ContractResponse execute(Long id){
        return new ContractResponse(contractService.findById(id));
    }
}
