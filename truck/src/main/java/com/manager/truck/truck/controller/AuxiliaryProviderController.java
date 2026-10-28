package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderRequest;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderStatusRequest;
import com.manager.truck.truck.domain.dto.request.AuxiliaryProviderUpdateRequest;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProviderResponse;
import com.manager.truck.truck.domain.dto.response.AuxiliaryProvidersResponse;
import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import com.manager.truck.truck.usecase.auxiliaryprovider.GetAllAuxiliaryProvidersUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.GetAuxiliaryProviderByIdUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.SaveAuxiliaryProviderUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.UpdateAuxiliaryProviderStatusUseCase;
import com.manager.truck.truck.usecase.auxiliaryprovider.UpdateAuxiliaryProviderUseCase;
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
@RequestMapping("/api/auxiliary-providers")
@RequiredArgsConstructor
@Tag(name = "Auxiliary Providers",
        description = "Proveedores ocasionales de auxilio (empresa o independiente); nunca son socios. "
                + "Solo ADMIN y SECRETARY administran")
public class AuxiliaryProviderController {

    private final GetAllAuxiliaryProvidersUseCase getAllAuxiliaryProvidersUseCase;
    private final GetAuxiliaryProviderByIdUseCase getAuxiliaryProviderByIdUseCase;
    private final SaveAuxiliaryProviderUseCase saveAuxiliaryProviderUseCase;
    private final UpdateAuxiliaryProviderUseCase updateAuxiliaryProviderUseCase;
    private final UpdateAuxiliaryProviderStatusUseCase updateAuxiliaryProviderStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los proveedores de auxilio con filtros opcionales por tipo y nombre")
    public AuxiliaryProvidersResponse getAll(
            @Parameter(description = "Filtra por tipo de proveedor") @RequestParam(required = false)
            AuxiliaryProviderKind kind,
            @Parameter(description = "Filtra por nombre (parcial, sin distinguir mayúsculas)")
            @RequestParam(required = false) String name,
            @PageableDefault(size = 20, sort = "providerId") Pageable pageable) {
        return getAllAuxiliaryProvidersUseCase.execute(kind, name, pageable);
    }

    @GetMapping("/{providerId}")
    @Operation(summary = "Consulta un proveedor de auxilio por id")
    public AuxiliaryProviderResponse getById(
            @Parameter(description = "Identificador del proveedor de auxilio") @PathVariable Long providerId) {
        return getAuxiliaryProviderByIdUseCase.execute(providerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un proveedor de auxilio (ADMIN o SECRETARY)")
    public AuxiliaryProviderResponse create(@Valid @RequestBody AuxiliaryProviderRequest request) {
        return saveAuxiliaryProviderUseCase.execute(request);
    }

    @PutMapping("/{providerId}")
    @Operation(summary = "Actualiza los datos de un proveedor de auxilio sin cambiar su estado")
    public AuxiliaryProviderResponse update(
            @Parameter(description = "Identificador del proveedor de auxilio") @PathVariable Long providerId,
            @Valid @RequestBody AuxiliaryProviderUpdateRequest request) {
        return updateAuxiliaryProviderUseCase.execute(providerId, request);
    }

    @PutMapping("/{providerId}/status")
    @Operation(summary = "Cambia el estado del proveedor de auxilio")
    public AuxiliaryProviderResponse updateStatus(
            @Parameter(description = "Identificador del proveedor de auxilio") @PathVariable Long providerId,
            @Valid @RequestBody AuxiliaryProviderStatusRequest request) {
        return updateAuxiliaryProviderStatusUseCase.execute(providerId, request);
    }
}
