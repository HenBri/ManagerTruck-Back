package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetChargeByIdUseCase {
    @Autowired
    private ChargeService chargeService;

    public ChargeResponse execute(Long id){
        return new ChargeResponse(chargeService.findById(id));
    }
}
