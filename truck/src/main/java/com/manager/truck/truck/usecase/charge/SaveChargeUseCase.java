package com.manager.truck.truck.usecase.charge;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ChargeService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveChargeUseCase {

    private final ChargeService chargeService;
    private final ContractService contractService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ChargeResponse execute(Long companyId, ChargeRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear cargas en una empresa inactiva");
        }
        Contract contract = contractService.findByCompanyIdAndContractId(companyId, request.getContractId());
        if (contract.getContractState() != ContractState.ACTIVE) {
            throw new ConflictException("Solo se pueden crear cargas sobre un contrato ACTIVO; "
                    + "el contrato está en estado " + contract.getContractState());
        }
        Charge charge = new Charge();
        charge.setCompanyId(companyId);
        charge.setContractId(contract.getContractId());
        charge.setChargeDescription(request.getChargeDescription());
        charge.setChargeType(request.getChargeType());
        charge.setChargeState(ChargeState.PENDING);
        Charge saved = chargeService.save(charge);
        return ChargeResponse.from(saved);
    }
}
