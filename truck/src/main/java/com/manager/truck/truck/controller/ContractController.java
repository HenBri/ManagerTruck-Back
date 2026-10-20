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
@RequestMapping("/api/companies/{companyId}/contracts")
@RequiredArgsConstructor
@Tag(name = "Contracts", description = "Contratos comerciales por cliente y empresa; "
        + "solo SUPER_ADMIN y SECRETARY administran (PARTNER/DRIVER sin acceso)")
public class ContractController {

    private final GetAllContractsUseCase getAllContractsUseCase;
    private final GetContractByIdUseCase getContractByIdUseCase;
    private final SaveContractUseCase saveContractUseCase;
    private final UpdateContractUseCase updateContractUseCase;
    private final UpdateContractStatusUseCase updateContractStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los contratos de una empresa con filtros opcionales por cliente y estado")
    public ContractsResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Filtra por cliente dueño del contrato") @RequestParam(required = false) Long clientId,
            @Parameter(description = "Filtra por estado del contrato") @RequestParam(required = false) ContractState state,
            @PageableDefault(size = 20, sort = "contractId") Pageable pageable) {
        return getAllContractsUseCase.execute(companyId, clientId, state, pageable);
    }

    @GetMapping("/{contractId}")
    @Operation(summary = "Consulta un contrato por id dentro de la empresa")
    public ContractResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del contrato") @PathVariable Long contractId) {
        return getContractByIdUseCase.execute(companyId, contractId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un contrato para un cliente de la empresa (SUPER_ADMIN o SECRETARY); "
            + "el cliente debe estar activo y en el mismo tenant")
    public ContractResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody ContractRequest request) {
        return saveContractUseCase.execute(companyId, request);
    }

    @PutMapping("/{contractId}")
    @Operation(summary = "Actualiza los datos escalares de un contrato sin cambiar cliente ni estado")
    public ContractResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del contrato") @PathVariable Long contractId,
            @Valid @RequestBody ContractUpdateRequest request) {
        return updateContractUseCase.execute(companyId, contractId, request);
    }

    @PutMapping("/{contractId}/status")
    @Operation(summary = "Cambia el estado del contrato con transiciones estrictas "
            + "(DRAFT→ACTIVE|CANCELLED, ACTIVE→COMPLETED|CANCELLED)")
    public ContractResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del contrato") @PathVariable Long contractId,
            @Valid @RequestBody ContractStatusRequest request) {
        return updateContractStatusUseCase.execute(companyId, contractId, request);
    }
}
