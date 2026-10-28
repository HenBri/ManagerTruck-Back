package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientService {

    Client save(Client client);

    Client update(Client client);

    Client findById(Long clientId);

    Optional<Client> findOptionalById(Long clientId);

    boolean existsByClientName(String clientName);

    boolean existsByClientNameAndClientIdNot(String clientName, Long clientId);

    Page<Client> findAll(Pageable pageable);
}
