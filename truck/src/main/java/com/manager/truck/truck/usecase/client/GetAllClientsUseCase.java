package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.dto.response.ClientsResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetAllClientsUseCase {

    private final ClientService clientService;
    private final CurrentUserProvider currentUserProvider;

    public ClientsResponse execute(Pageable pageable) {
        currentUserProvider.requireAdminOrSecretary();
        Page<Client> page = clientService.findAll(pageable);
        Page<ClientResponse> content = page.map(ClientResponse::from);
        return new ClientsResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
