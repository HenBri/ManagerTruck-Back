package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientStatusRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClientStatusUseCase {

    private final ClientService clientService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ClientResponse execute(Long companyId, Long clientId, ClientStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Client client = clientService.findByCompanyIdAndClientId(companyId, clientId);
        if (client.getClientState().equals(request.getClientState())) {
            throw new ConflictException("El cliente ya se encuentra con el estado " + request.getClientState());
        }
        client.setClientState(request.getClientState());
        Client saved = clientService.save(client);
        return ClientResponse.from(saved);
    }
}
