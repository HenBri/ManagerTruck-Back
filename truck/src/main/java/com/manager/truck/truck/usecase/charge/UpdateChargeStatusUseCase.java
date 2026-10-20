package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.request.ChargeStatusRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UpdateChargeStatusUseCase {

    private static final Map<ChargeState, Set<ChargeState>> ALLOWED_TRANSITIONS = Map.of(
            ChargeState.PENDING, Set.of(ChargeState.IN_TRANSIT, ChargeState.CANCELLED),
            ChargeState.IN_TRANSIT, Set.of(ChargeState.DELIVERED, ChargeState.CANCELLED),
            ChargeState.DELIVERED, Set.of(),
            ChargeState.CANCELLED, Set.of());

    private final ChargeService chargeService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ChargeResponse execute(Long companyId, Long chargeId, ChargeStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Charge charge = chargeService.findByCompanyIdAndChargeId(companyId, chargeId);
        ChargeState current = charge.getChargeState();
        ChargeState target = request.getStatus();
        if (current == target) {
            throw new ConflictException("La carga ya se encuentra en el estado " + current);
        }
        if (!ALLOWED_TRANSITIONS.get(current).contains(target)) {
            throw new ConflictException(
                    "Transición de estado inválida para la carga: " + current + " -> " + target);
        }
        charge.setChargeState(target);
        Charge saved = chargeService.save(charge);
        return ChargeResponse.from(saved);
    }
}
