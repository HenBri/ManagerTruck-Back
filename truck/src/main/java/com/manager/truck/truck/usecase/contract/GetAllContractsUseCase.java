package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllContractsUseCase {

    private final ContractService contractService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public ContractsResponse execute(Long companyId, Long clientId, ContractState state, Pageable pageable) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Page<Contract> page;
        if (clientId != null && state != null) {
            page = contractService.findByCompanyIdAndClientIdAndContractState(companyId, clientId, state, pageable);
        } else if (clientId != null) {
            page = contractService.findByCompanyIdAndClientId(companyId, clientId, pageable);
        } else if (state != null) {
            page = contractService.findByCompanyIdAndContractState(companyId, state, pageable);
        } else {
            page = contractService.findByCompanyId(companyId, pageable);
        }
        Page<ContractResponse> content = page.map(ContractResponse::from);
        return new ContractsResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
