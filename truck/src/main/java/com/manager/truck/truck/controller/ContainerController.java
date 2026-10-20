package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ContainerRequest;
import com.manager.truck.truck.domain.dto.request.ContainerStatusRequest;
import com.manager.truck.truck.domain.dto.request.ContainerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ContainerResponse;
import com.manager.truck.truck.domain.dto.response.ContainersResponse;
import com.manager.truck.truck.domain.enums.ContainerState;
import com.manager.truck.truck.usecase.container.GetAllContainersUseCase;
import com.manager.truck.truck.usecase.container.GetContainerByIdUseCase;
import com.manager.truck.truck.usecase.container.SaveContainerUseCase;
import com.manager.truck.truck.usecase.container.UpdateContainerStatusUseCase;
import com.manager.truck.truck.usecase.container.UpdateContainerUseCase;
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
@RequestMapping("/api/companies/{companyId}/containers")
@RequiredArgsConstructor
@Tag(name = "Containers", description = "Contenedores de una carga; código único por empresa. "
        + "Solo SUPER_ADMIN y SECRETARY administran (PARTNER/DRIVER sin acceso)")
public class ContainerController {

    private final GetAllContainersUseCase getAllContainersUseCase;
    private final GetContainerByIdUseCase getContainerByIdUseCase;
    private final SaveContainerUseCase saveContainerUseCase;
    private final UpdateContainerUseCase updateContainerUseCase;
    private final UpdateContainerStatusUseCase updateContainerStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los contenedores de una empresa con filtros opcionales por carga y estado")
    public ContainersResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Filtra por carga dueña del contenedor") @RequestParam(required = false) Long chargeId,
            @Parameter(description = "Filtra por estado del contenedor") @RequestParam(required = false) ContainerState state,
            @PageableDefault(size = 20, sort = "containerId") Pageable pageable) {
        return getAllContainersUseCase.execute(companyId, chargeId, state, pageable);
    }

    @GetMapping("/{containerId}")
    @Operation(summary = "Consulta un contenedor por id dentro de la empresa")
    public ContainerResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del contenedor") @PathVariable Long containerId) {
        return getContainerByIdUseCase.execute(companyId, containerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un contenedor para una carga no cancelada de la empresa "
            + "(SUPER_ADMIN o SECRETARY); el código debe ser único por empresa")
    public ContainerResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody ContainerRequest request) {
        return saveContainerUseCase.execute(companyId, request);
    }

    @PutMapping("/{containerId}")
    @Operation(summary = "Actualiza los datos escalares de un contenedor sin cambiar carga ni estado")
    public ContainerResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del contenedor") @PathVariable Long containerId,
            @Valid @RequestBody ContainerUpdateRequest request) {
        return updateContainerUseCase.execute(companyId, containerId, request);
    }

    @PutMapping("/{containerId}/status")
    @Operation(summary = "Cambia el estado del contenedor con transiciones estrictas "
            + "(AVAILABLE→LOADED→IN_TRANSIT→DELIVERED)")
    public ContainerResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del contenedor") @PathVariable Long containerId,
            @Valid @RequestBody ContainerStatusRequest request) {
        return updateContainerStatusUseCase.execute(companyId, containerId, request);
    }
}
