package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetClientByIdUseCase {

    private final ClientService clientService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public ClientResponse execute(Long companyId, Long clientId) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Client client = clientService.findByCompanyIdAndClientId(companyId, clientId);
        return ClientResponse.from(client);
    }
}
