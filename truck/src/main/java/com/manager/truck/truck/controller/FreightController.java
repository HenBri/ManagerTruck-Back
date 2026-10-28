package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.FreightRequest;
import com.manager.truck.truck.domain.dto.request.FreightStatusRequest;
import com.manager.truck.truck.domain.dto.request.FreightUpdateRequest;
import com.manager.truck.truck.domain.dto.response.FreightResponse;
import com.manager.truck.truck.domain.dto.response.FreightsResponse;
import com.manager.truck.truck.domain.enums.FreightState;
import com.manager.truck.truck.usecase.freight.GetAllFreightsUseCase;
import com.manager.truck.truck.usecase.freight.GetFreightByIdUseCase;
import com.manager.truck.truck.usecase.freight.SaveFreightUseCase;
import com.manager.truck.truck.usecase.freight.UpdateFreightStatusUseCase;
import com.manager.truck.truck.usecase.freight.UpdateFreightUseCase;
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
@RequestMapping("/api/freights")
@RequiredArgsConstructor
@Tag(name = "Freights", description = "Gestión de fletes")
public class FreightController {

    private final GetAllFreightsUseCase getAllFreightsUseCase;
    private final GetFreightByIdUseCase getFreightByIdUseCase;
    private final SaveFreightUseCase saveFreightUseCase;
    private final UpdateFreightUseCase updateFreightUseCase;
    private final UpdateFreightStatusUseCase updateFreightStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los fletes con filtros opcionales por contrato, camión y estado")
    public FreightsResponse getAll(
            @Parameter(description = "Filtro opcional por contrato") @RequestParam(required = false) Long contractId,
            @Parameter(description = "Filtro opcional por camión") @RequestParam(required = false) Long truckId,
            @Parameter(description = "Filtro opcional por estado del flete") @RequestParam(required = false) FreightState state,
            @PageableDefault(size = 20, sort = "freightId") Pageable pageable) {
        return getAllFreightsUseCase.execute(contractId, truckId, state, pageable);
    }

    @GetMapping("/{freightId}")
    @Operation(summary = "Consulta un flete por id")
    public FreightResponse getById(
            @Parameter(description = "Identificador del flete") @PathVariable Long freightId) {
        return getFreightByIdUseCase.execute(freightId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un nuevo flete")
    public FreightResponse create(@Valid @RequestBody FreightRequest request) {
        return saveFreightUseCase.execute(request);
    }

    @PutMapping("/{freightId}")
    @Operation(summary = "Actualiza los datos de un flete sin cambiar su estado")
    public FreightResponse update(
            @Parameter(description = "Identificador del flete") @PathVariable Long freightId,
            @Valid @RequestBody FreightUpdateRequest request) {
        return updateFreightUseCase.execute(freightId, request);
    }

    @PutMapping("/{freightId}/status")
    @Operation(summary = "Cambia el estado de un flete")
    public FreightResponse updateStatus(
            @Parameter(description = "Identificador del flete") @PathVariable Long freightId,
            @Valid @RequestBody FreightStatusRequest request) {
        return updateFreightStatusUseCase.execute(freightId, request);
    }
}

