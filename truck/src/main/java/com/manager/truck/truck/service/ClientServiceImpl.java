package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client update(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public Client findById(Long clientId) {
        return clientRepository.findActiveById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id " + clientId));
    }

    @Override
    public Optional<Client> findOptionalById(Long clientId) {
        return clientRepository.findActiveById(clientId);
    }

    @Override
    public boolean existsByClientName(String clientName) {
        return clientRepository.existsByClientName(clientName);
    }

    @Override
    public boolean existsByClientNameAndClientIdNot(String clientName, Long clientId) {
        return clientRepository.existsByClientNameAndClientIdNot(clientName, clientId);
    }

    @Override
    public Page<Client> findAll(Pageable pageable) {
        return clientRepository.findAllActive(pageable);
    }
}
