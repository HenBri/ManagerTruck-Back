package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientService {

    Client save(Client client);

    Client findByCompanyIdAndClientId(Long companyId, Long clientId);

    Optional<Client> findOptionalByCompanyIdAndClientId(Long companyId, Long clientId);

    Page<Client> findByCompanyId(Long companyId, Pageable pageable);

    Page<Client> findByCompanyIdAndClientState(Long companyId, Boolean clientState, Pageable pageable);
}
