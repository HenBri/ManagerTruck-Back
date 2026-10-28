package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetClientByIdUseCase {

    private final ClientService clientService;
    private final CurrentUserProvider currentUserProvider;

    public ClientResponse execute(Long clientId) {
        currentUserProvider.requireAdminOrSecretary();
        Client client = clientService.findById(clientId);
        return ClientResponse.from(client);
    }
}
