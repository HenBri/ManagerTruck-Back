package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.TruckRequest;
import com.manager.truck.truck.domain.dto.request.TruckStatusRequest;
import com.manager.truck.truck.domain.dto.request.TruckUpdateRequest;
import com.manager.truck.truck.domain.dto.response.TruckResponse;
import com.manager.truck.truck.domain.dto.response.TrucksResponse;
import com.manager.truck.truck.usecase.truck.GetAllTrucksUseCase;
import com.manager.truck.truck.usecase.truck.GetTruckByIdUseCase;
import com.manager.truck.truck.usecase.truck.SaveTruckUseCase;
import com.manager.truck.truck.usecase.truck.UpdateTruckStatusUseCase;
import com.manager.truck.truck.usecase.truck.UpdateTruckUseCase;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trucks")
@RequiredArgsConstructor
@Tag(name = "Trucks", description = "Camiones de socios. ADMIN y SECRETARY administran, PARTNER consulta sus camiones")
public class TruckController {

    private final GetAllTrucksUseCase getAllTrucksUseCase;
    private final GetTruckByIdUseCase getTruckByIdUseCase;
    private final SaveTruckUseCase saveTruckUseCase;
    private final UpdateTruckUseCase updateTruckUseCase;
    private final UpdateTruckStatusUseCase updateTruckStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los camiones; ADMIN/SECRETARY ven todos, PARTNER solo los de su propiedad")
    public TrucksResponse getAll(@PageableDefault(size = 20, sort = "truckId") Pageable pageable) {
        return getAllTrucksUseCase.execute(pageable);
    }

    @GetMapping("/{truckId}")
    @Operation(summary = "Consulta un camión por id")
    public TruckResponse getById(
            @Parameter(description = "Identificador del camión") @PathVariable Long truckId) {
        return getTruckByIdUseCase.execute(truckId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un camión con socio propietario obligatorio")
    public TruckResponse create(@Valid @RequestBody TruckRequest request) {
        return saveTruckUseCase.execute(request);
    }

    @PutMapping("/{truckId}")
    @Operation(summary = "Actualiza los datos de un camión sin cambiar de socio")
    public TruckResponse update(
            @Parameter(description = "Identificador del camión") @PathVariable Long truckId,
            @Valid @RequestBody TruckUpdateRequest request) {
        return updateTruckUseCase.execute(truckId, request);
    }

    @PutMapping("/{truckId}/status")
    @Operation(summary = "Cambia el estado del camión")
    public TruckResponse updateStatus(
            @Parameter(description = "Identificador del camión") @PathVariable Long truckId,
            @Valid @RequestBody TruckStatusRequest request) {
        return updateTruckStatusUseCase.execute(truckId, request);
    }
}
