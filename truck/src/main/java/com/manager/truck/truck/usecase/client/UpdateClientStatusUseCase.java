package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientStatusRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClientStatusUseCase {

    private final ClientService clientService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ClientResponse execute(Long clientId, ClientStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Client client = clientService.findById(clientId);
        String target = request.getState();
        if (target.equalsIgnoreCase(client.getState())) {
            throw new ConflictException("El cliente ya se encuentra con el estado " + target);
        }
        client.setState(target);
        Client saved = clientService.update(client);
        return ClientResponse.from(saved);
    }
}
