package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveClientUseCase {

    private final ClientService clientService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ClientResponse execute(ClientRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        String name = request.getClientName().trim();
        if (clientService.existsByClientName(name)) {
            throw new ConflictException("Ya existe un cliente con el nombre " + name);
        }
        Client client = new Client();
        client.setClientName(name);
        client.setClientCi(request.getClientCi());
        client.setClientEmail(request.getClientEmail());
        client.setClientPhone(request.getClientPhone());
        client.setClientAddress(request.getClientAddress());
        client.setTaxId(request.getTaxId());
        client.setContactPerson(request.getContactPerson());
        client.setContactPhone(request.getContactPhone());
        client.setState("ACTIVE");
        Client saved = clientService.save(client);
        return ClientResponse.from(saved);
    }
}
