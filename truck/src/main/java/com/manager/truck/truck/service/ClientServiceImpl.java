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
    public Client findByCompanyIdAndClientId(Long companyId, Long clientId) {
        return clientRepository.findByCompanyIdAndClientId(companyId, clientId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado con el id " + clientId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Client> findOptionalByCompanyIdAndClientId(Long companyId, Long clientId) {
        return clientRepository.findByCompanyIdAndClientId(companyId, clientId);
    }

    @Override
    public Page<Client> findByCompanyId(Long companyId, Pageable pageable) {
        return clientRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<Client> findByCompanyIdAndClientState(Long companyId, Boolean clientState, Pageable pageable) {
        return clientRepository.findByCompanyIdAndClientState(companyId, clientState, pageable);
    }
}
