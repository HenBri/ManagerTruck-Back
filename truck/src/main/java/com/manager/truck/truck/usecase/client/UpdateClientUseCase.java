package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateClientUseCase {

    private final ClientService clientService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ClientResponse execute(Long clientId, ClientUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Client client = clientService.findById(clientId);
        String name = request.getClientName().trim();
        if (!name.equalsIgnoreCase(client.getClientName())
                && clientService.existsByClientNameAndClientIdNot(name, clientId)) {
            throw new ConflictException("Ya existe un cliente con el nombre " + name);
        }
        client.setClientName(name);
        client.setClientCi(request.getClientCi());
        client.setClientEmail(request.getClientEmail());
        client.setClientPhone(request.getClientPhone());
        client.setClientAddress(request.getClientAddress());
        client.setTaxId(request.getTaxId());
        client.setContactPerson(request.getContactPerson());
        client.setContactPhone(request.getContactPhone());
        Client saved = clientService.update(client);
        return ClientResponse.from(saved);
    }
}
