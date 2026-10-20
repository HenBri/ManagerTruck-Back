package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetChargeByIdUseCase {

    private final ChargeService chargeService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public ChargeResponse execute(Long companyId, Long chargeId) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Charge charge = chargeService.findByCompanyIdAndChargeId(companyId, chargeId);
        return ChargeResponse.from(charge);
    }
}
