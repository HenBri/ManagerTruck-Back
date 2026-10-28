package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.dto.response.ChargesResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllChargesUseCase {

    private final ChargeService chargeService;
    private final CurrentUserProvider currentUserProvider;

    public ChargesResponse execute(Long contractId, Pageable pageable) {
        currentUserProvider.requireAdminOrSecretary();
        Page<Charge> page;
        if (contractId != null) {
            page = chargeService.findByContractId(contractId, pageable);
        } else {
            page = chargeService.findAll(pageable);
        }
        Page<ChargeResponse> content = page.map(ChargeResponse::from);
        return new ChargesResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
