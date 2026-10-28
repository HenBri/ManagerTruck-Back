package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.request.ChargeStatusRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateChargeStatusUseCase {

    private final ChargeService chargeService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ChargeResponse execute(Long chargeId, ChargeStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Charge charge = chargeService.findById(chargeId);
        String current = charge.getState();
        String target = request.getState();
        if (target.equalsIgnoreCase(current)) {
            throw new ConflictException("La carga ya se encuentra en el estado " + current);
        }
        charge.setState(target);
        Charge saved = chargeService.update(charge);
        return ChargeResponse.from(saved);
    }
}
