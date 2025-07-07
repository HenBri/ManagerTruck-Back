package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.dto.response.ClientsResponse;
import com.manager.truck.truck.usecase.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clients")
public class ClientController {
    @Autowired
    private SaveClientUseCase saveClientUseCase;
    @Autowired
    private GetClientByIdUseCase getClientByIdUseCase;
    @Autowired
    private GetAllClientUseCase getAllClientUseCase;
    @Autowired
    private UpdateClientUseCase updateClientUseCase;
    @Autowired
    private DeleteClientUseCase deleteClientUseCase;

    @PostMapping
    public ClientResponse createClient(@RequestBody ClientRequest clientRequest) {
        return saveClientUseCase.execute(clientRequest);
    }

    @GetMapping("/{id}")
    public ClientResponse getClientById(@PathVariable Long id) {
        return getClientByIdUseCase.execute(id);
    }

    @GetMapping
    public ClientsResponse getAllClients() {
        return getAllClientUseCase.execute();
    }


    @PutMapping("/{id}")
    public ClientResponse updateClient(@PathVariable Long id, @RequestBody ClientRequest clientRequest) {
        return updateClientUseCase.execute(id, clientRequest);
    }
    @DeleteMapping("/{id}")
    public void deleteClient(@PathVariable Long id){
        deleteClientUseCase.execute(id);
    }
}