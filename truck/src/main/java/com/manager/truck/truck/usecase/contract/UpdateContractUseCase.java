package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.request.ContractUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateContractUseCase {

    private final ContractService contractService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ContractResponse execute(Long companyId, Long contractId, ContractUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Contract contract = contractService.findByCompanyIdAndContractId(companyId, contractId);
        contract.setContractAmount(request.getContractAmount());
        contract.setContractDate(request.getContractDate());
        contract.setContractDescription(request.getContractDescription());
        contract.setContractType(request.getContractType());
        Contract saved = contractService.save(contract);
        return ContractResponse.from(saved);
    }
}
