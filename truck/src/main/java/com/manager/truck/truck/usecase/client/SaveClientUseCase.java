package com.manager.truck.truck.usecase.client;

import com.manager.truck.truck.domain.Client;
import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.ClientService;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveClientUseCase {

    private final ClientService clientService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public ClientResponse execute(Long companyId, ClientRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear clientes en una empresa inactiva");
        }
        Client client = new Client();
        client.setCompanyId(companyId);
        client.setClientName(request.getClientName().trim());
        client.setClientCi(request.getClientCi());
        client.setClientEmail(request.getClientEmail());
        client.setClientPhone(request.getClientPhone());
        client.setClientAddress(request.getClientAddress());
        client.setClientState(Boolean.TRUE);
        Client saved = clientService.save(client);
        return ClientResponse.from(saved);
    }
}
