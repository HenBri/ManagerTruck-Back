package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChargeByIdUseCase {

    private final ChargeService chargeService;
    private final CurrentUserProvider currentUserProvider;

    public ChargeResponse execute(Long chargeId) {
        currentUserProvider.requireAdminOrSecretary();
        Charge charge = chargeService.findById(chargeId);
        return ChargeResponse.from(charge);
    }
}
