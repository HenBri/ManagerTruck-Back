package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.service.ChargeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateChargeUseCase {
    @Autowired
    private ChargeService chargeService;

    public ChargeResponse execute(Long id, ChargeRequest chargeRequest){
        Charge charge = chargeBuild(id, chargeRequest);
        chargeService.save(charge);
        return chargeResponseBuild(charge);
    }

    private Charge chargeBuild(Long id, ChargeRequest chargeRequest){
        Charge charge = chargeService.findById(id);
        charge.setDescription(chargeRequest.getDescription());
        charge.setType(chargeRequest.getType());
        return charge;
    }
    private ChargeResponse chargeResponseBuild(Charge charge){
        return new ChargeResponse(charge);
    }
}
