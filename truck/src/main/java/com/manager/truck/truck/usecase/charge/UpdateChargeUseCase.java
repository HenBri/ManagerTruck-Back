package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.request.ChargeUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateChargeUseCase {

    private final ChargeService chargeService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ChargeResponse execute(Long chargeId, ChargeUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Charge charge = chargeService.findById(chargeId);
        if (request.getChargeDescription() != null) charge.setChargeDescription(request.getChargeDescription());
        if (request.getChargeType() != null) charge.setChargeType(request.getChargeType());
        if (request.getCargoReference() != null) charge.setCargoReference(request.getCargoReference());
        if (request.getGoodsDescription() != null) charge.setGoodsDescription(request.getGoodsDescription());
        if (request.getHazardousGoods() != null) charge.setHazardousGoods(request.getHazardousGoods());
        if (request.getHandlingInstructions() != null) charge.setHandlingInstructions(request.getHandlingInstructions());
        Charge saved = chargeService.update(charge);
        return ChargeResponse.from(saved);
    }
}
