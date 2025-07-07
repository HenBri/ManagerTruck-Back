package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteChargeUseCase {
    @Autowired
    private ChargeService chargeService;

    public void execute(Long id){
        Charge charge = chargeService.getById(id);
        charge.setState(false);
        chargeService.save(charge);
    }
}
