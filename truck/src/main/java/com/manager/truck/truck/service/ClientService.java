package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Client;

import java.util.List;

public interface ClientService {
    Client save(Client client);
    Client GetById(Long id);
    List<Client>  GetAll();
    Client update(Client client);


}
