package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.repository.ClientRepository;
import com.manager.truck.truck.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GetClientByIdUseCase {
    @Autowired
    private ClientService clientService;

    public ClientResponse execute(Long id) {
        Client client =  clientService.GetById(id) ;
        return buildClientResponse(client);
    }
    public ClientResponse buildClientResponse(Client client){
        return new ClientResponse(client);
    }
}