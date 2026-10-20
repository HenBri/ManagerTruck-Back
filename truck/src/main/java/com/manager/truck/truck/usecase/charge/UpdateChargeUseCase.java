package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.request.ChargeUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateChargeUseCase {

    private final ChargeService chargeService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ChargeResponse execute(Long companyId, Long chargeId, ChargeUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Charge charge = chargeService.findByCompanyIdAndChargeId(companyId, chargeId);
        charge.setChargeDescription(request.getChargeDescription());
        charge.setChargeType(request.getChargeType());
        Charge saved = chargeService.save(charge);
        return ChargeResponse.from(saved);
    }
}
