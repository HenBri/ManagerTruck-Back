package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteClientUseCase {
    @Autowired
    private ClientService clientService;

    public void execute(Long id){
        Client client=  clientService.GetById(id);
        client.setState(false);
        clientService.save(client);
    }

}
