package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.dto.response.ChargesResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllChargesUseCase {

    private final ChargeService chargeService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public ChargesResponse execute(Long companyId, Long contractId, ChargeState state, Pageable pageable) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Page<Charge> page;
        if (contractId != null && state != null) {
            page = chargeService.findByCompanyIdAndContractIdAndChargeState(companyId, contractId, state, pageable);
        } else if (contractId != null) {
            page = chargeService.findByCompanyIdAndContractId(companyId, contractId, pageable);
        } else if (state != null) {
            page = chargeService.findByCompanyIdAndChargeState(companyId, state, pageable);
        } else {
            page = chargeService.findByCompanyId(companyId, pageable);
        }
        Page<ChargeResponse> content = page.map(ChargeResponse::from);
        return new ChargesResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
