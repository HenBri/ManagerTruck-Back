package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.dto.response.ClientsResponse;
import com.manager.truck.truck.repository.ClientRepository;
import com.manager.truck.truck.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetAllClientUseCase {
    @Autowired
    private ClientService clientService;

    public ClientsResponse execute() {
        List<Client> clients = clientService.GetAll();
        return buildClientsResponse(clients);
    }

    private ClientsResponse buildClientsResponse(List<Client> clients){
        return new ClientsResponse(clients);
    }
}