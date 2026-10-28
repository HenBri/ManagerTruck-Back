package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ClientRequest;
import com.manager.truck.truck.domain.dto.request.ClientStatusRequest;
import com.manager.truck.truck.domain.dto.request.ClientUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ClientResponse;
import com.manager.truck.truck.domain.dto.response.ClientsResponse;
import com.manager.truck.truck.usecase.client.GetAllClientsUseCase;
import com.manager.truck.truck.usecase.client.GetClientByIdUseCase;
import com.manager.truck.truck.usecase.client.SaveClientUseCase;
import com.manager.truck.truck.usecase.client.UpdateClientStatusUseCase;
import com.manager.truck.truck.usecase.client.UpdateClientUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Clientes de la empresa. Solo ADMIN y SECRETARY administran")
public class ClientController {

    private final GetAllClientsUseCase getAllClientsUseCase;
    private final GetClientByIdUseCase getClientByIdUseCase;
    private final SaveClientUseCase saveClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final UpdateClientStatusUseCase updateClientStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los clientes")
    public ClientsResponse getAll(@PageableDefault(size = 20, sort = "clientId") Pageable pageable) {
        return getAllClientsUseCase.execute(pageable);
    }

    @GetMapping("/{clientId}")
    @Operation(summary = "Consulta un cliente por id")
    public ClientResponse getById(
            @Parameter(description = "Identificador del cliente") @PathVariable Long clientId) {
        return getClientByIdUseCase.execute(clientId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un cliente")
    public ClientResponse create(@Valid @RequestBody ClientRequest request) {
        return saveClientUseCase.execute(request);
    }

    @PutMapping("/{clientId}")
    @Operation(summary = "Actualiza los datos de un cliente")
    public ClientResponse update(
            @Parameter(description = "Identificador del cliente") @PathVariable Long clientId,
            @Valid @RequestBody ClientUpdateRequest request) {
        return updateClientUseCase.execute(clientId, request);
    }

    @PutMapping("/{clientId}/status")
    @Operation(summary = "Activa o desactiva un cliente")
    public ClientResponse updateStatus(
            @Parameter(description = "Identificador del cliente") @PathVariable Long clientId,
            @Valid @RequestBody ClientStatusRequest request) {
        return updateClientStatusUseCase.execute(clientId, request);
    }
}
