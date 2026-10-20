package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClientUseCase {

    private final ClientService clientService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ClientResponse execute(Long companyId, Long clientId, ClientUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Client client = clientService.findByCompanyIdAndClientId(companyId, clientId);
        client.setClientName(request.getClientName().trim());
        client.setClientCi(request.getClientCi());
        client.setClientEmail(request.getClientEmail());
        client.setClientPhone(request.getClientPhone());
        client.setClientAddress(request.getClientAddress());
        Client saved = clientService.save(client);
        return ClientResponse.from(saved);
    }
}
