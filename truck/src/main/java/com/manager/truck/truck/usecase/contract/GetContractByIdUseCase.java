package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetContractByIdUseCase {

    private final ContractService contractService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public ContractResponse execute(Long companyId, Long contractId) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Contract contract = contractService.findByCompanyIdAndContractId(companyId, contractId);
        return ContractResponse.from(contract);
    }
}
