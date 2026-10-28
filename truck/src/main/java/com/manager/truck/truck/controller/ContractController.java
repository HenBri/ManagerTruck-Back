package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ContractRequest;
import com.manager.truck.truck.domain.dto.request.ContractStatusRequest;
import com.manager.truck.truck.domain.dto.request.ContractUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContractResponse;
import com.manager.truck.truck.domain.dto.response.ContractsResponse;
import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.usecase.contract.GetAllContractsUseCase;
import com.manager.truck.truck.usecase.contract.GetContractByIdUseCase;
import com.manager.truck.truck.usecase.contract.SaveContractUseCase;
import com.manager.truck.truck.usecase.contract.UpdateContractStatusUseCase;
import com.manager.truck.truck.usecase.contract.UpdateContractUseCase;
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
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "Contratos comerciales por cliente. Solo ADMIN y SECRETARY administran")
public class ContractController {

    private final GetAllContractsUseCase getAllContractsUseCase;
    private final GetContractByIdUseCase getContractByIdUseCase;
    private final SaveContractUseCase saveContractUseCase;
    private final UpdateContractUseCase updateContractUseCase;
    private final UpdateContractStatusUseCase updateContractStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los contratos con filtro opcional por cliente")
    public ContractsResponse getAll(
            @Parameter(description = "Filtra por cliente dueño del contrato") @RequestParam(required = false) Long clientId,
            @PageableDefault(size = 20, sort = "contractId") Pageable pageable) {
        return getAllContractsUseCase.execute(clientId, pageable);
    }

    @GetMapping("/{contractId}")
    @Operation(summary = "Consulta un contrato por id")
    public ContractResponse getById(
            @Parameter(description = "Identificador del contrato") @PathVariable Long contractId) {
        return getContractByIdUseCase.execute(contractId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un contrato para un cliente")
    public ContractResponse create(@Valid @RequestBody ContractRequest request) {
        return saveContractUseCase.execute(request);
    }

    @PutMapping("/{contractId}")
    @Operation(summary = "Actualiza los datos de un contrato sin cambiar su estado")
    public ContractResponse update(
            @Parameter(description = "Identificador del contrato") @PathVariable Long contractId,
            @Valid @RequestBody ContractUpdateRequest request) {
        return updateContractUseCase.execute(contractId, request);
    }

    @PutMapping("/{contractId}/status")
    @Operation(summary = "Cambia el estado del contrato")
    public ContractResponse updateStatus(
            @Parameter(description = "Identificador del contrato") @PathVariable Long contractId,
            @Valid @RequestBody ContractStatusRequest request) {
        return updateContractStatusUseCase.execute(contractId, request);
    }
}
