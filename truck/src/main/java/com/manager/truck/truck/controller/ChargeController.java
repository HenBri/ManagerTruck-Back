package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.dto.response.ChargesResponse;
import com.manager.truck.truck.usecase.charge.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ChargeController {
    @Autowired
    private DeleteChargeUseCase deleteChargeUseCase;
    @Autowired
    private GetAllChargeUseCase getAllChargeUseCase;
    @Autowired
    private GetChargeByIdUseCase getChargeByIdUseCase;
    @Autowired
    private SaveChargeUseCase saveChargeUseCase;
    @Autowired
    private UpdateChargeUseCase updateChargeUseCase;

    @PostMapping("saveCharge")
    public ChargeResponse save(@RequestBody ChargeRequest chargeRequest){
        return saveChargeUseCase.execute(chargeRequest);
    }
    @GetMapping("getAllCharge")
    public ChargesResponse getAll(){
        return getAllChargeUseCase.execute();
    }
    @GetMapping("getChargeById/{id}")
    public ChargeResponse getChargeById(@PathVariable Long id){
        return getChargeByIdUseCase.execute(id);
    }
    @PutMapping("updateCharge/{id}")
    public ChargeResponse update(@PathVariable Long id, @RequestBody ChargeRequest chargeRequest){
        return updateChargeUseCase.execute(id, chargeRequest);
    }
    @DeleteMapping("deleteCharge/{id}")
    public void delete(@PathVariable Long id){
        deleteChargeUseCase.execute(id);
    }
}
