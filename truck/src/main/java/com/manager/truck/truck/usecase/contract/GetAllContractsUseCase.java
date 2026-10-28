package com.manager.truck.truck.usecase.contract;

import com.manager.truck.truck.domain.Contract;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllContractsUseCase {

    private final ContractService contractService;
    private final CurrentUserProvider currentUserProvider;

    public ContractsResponse execute(Long clientId, Pageable pageable) {
        currentUserProvider.requireAdminOrSecretary();
        Page<Contract> page;
        if (clientId != null) {
            page = contractService.findByClientId(clientId, pageable);
        } else {
            page = contractService.findAll(pageable);
        }
        Page<ContractResponse> content = page.map(ContractResponse::from);
        return new ContractsResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
