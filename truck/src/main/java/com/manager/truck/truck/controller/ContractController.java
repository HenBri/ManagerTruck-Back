package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.usecase.contract.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController("api/contract")
public class ContractController {
    @Autowired
    private DeleteContractUseCase deleteContractUseCase;
    @Autowired
    private GetAllContractUseCase getAllContractUseCase;
    @Autowired
    private SaveContractUseCase saveContractUseCase;
    @Autowired
    private GetContractByIdUseCase getContractByIdUseCase;
    @Autowired
    private UpdateContractUseCase updateContractUseCase;

    @PostMapping("saveContract")
    public ContractResponse save(@RequestBody ContractRequest contractRequest){
        return saveContractUseCase.execute(contractRequest);
    }
    @GetMapping("getAllContract")
    public ContractsResponse getAll(){
        return getAllContractUseCase.execute();
    }
    @GetMapping("getContractById/{id}")
    public ContractResponse getContractById(@PathVariable Long id){
        return getContractByIdUseCase.execute(id);
    }
    @PutMapping("updateContract/{id}")
    public ContractResponse update(@PathVariable Long id, @RequestBody ContractRequest contractRequest){
        return updateContractUseCase.execute(id, contractRequest);
    }
    @DeleteMapping("deleteContract/{id}")
    public void delete(@PathVariable Long id){
        deleteContractUseCase.execute(id);
    }
}
