package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientServiceImpl implements ClientService{
    @Autowired
    private ClientRepository clientRepository;


    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client GetById(Long id) {
        return clientRepository.getById(id);
    }

    @Override
    public List<Client> GetAll() {
        return clientRepository.findAll();
    }

    @Override
    public Client update(Client client) {
        return clientRepository.save(client);
    }

}
