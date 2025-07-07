package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.dto.response.ChargesResponse;
import com.manager.truck.truck.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetAllChargeUseCase {
    @Autowired
    private ChargeService chargeService;

    public ChargesResponse execute(){
        return new ChargesResponse(chargeService.findAll());
    }
}
