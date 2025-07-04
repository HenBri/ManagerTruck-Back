package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.repository.ClientRepository;
import com.manager.truck.truck.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveClientUseCase {
    @Autowired
    private ClientService clientService;

    public ClientResponse execute(ClientRequest clientRequest) {
        Client client = buildClient(clientRequest);

        clientService.save(client);


        return buildClientResponse(client);
    }

    public Client buildClient(ClientRequest clientRequest){
        Client client = new Client();
        client.setName(clientRequest.getName());
        client.setPhone(clientRequest.getPhone());
        client.setEmail(clientRequest.getEmail());
        client.setAddress(clientRequest.getAddress());

        return client;
    }

    public ClientResponse buildClientResponse(Client client){
        return new ClientResponse(client);
    }



}