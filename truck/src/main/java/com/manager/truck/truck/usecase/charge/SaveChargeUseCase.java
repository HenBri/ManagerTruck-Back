package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveChargeUseCase {

    private final ChargeService chargeService;
    private final ContractService contractService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ChargeResponse execute(ChargeRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Contract contract = contractService.findById(request.getContractId());
        if (contract.getState() == ContractState.CANCELADO) {
            throw new ConflictException("No se pueden crear cargas sobre un contrato CANCELADO");
        }
        Charge charge = new Charge();
        charge.setContractId(contract.getContractId());
        charge.setChargeDescription(request.getChargeDescription());
        charge.setChargeType(request.getChargeType());
        charge.setCargoReference(request.getCargoReference());
        charge.setGoodsDescription(request.getGoodsDescription());
        charge.setHazardousGoods(Boolean.TRUE.equals(request.getHazardousGoods()));
        charge.setHandlingInstructions(request.getHandlingInstructions());
        charge.setState("PENDING");
        Charge saved = chargeService.save(charge);
        return ChargeResponse.from(saved);
    }
}
