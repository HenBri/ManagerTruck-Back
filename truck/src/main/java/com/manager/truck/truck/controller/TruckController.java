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
@RequestMapping("/api/companies/{companyId}/trucks")
@RequiredArgsConstructor
@Tag(name = "Trucks", description = "Camiones de socios por empresa (la empresa no es propietaria); "
        + "SECRETARY administra, PARTNER consulta sus propios camiones")
public class TruckController {

    private final GetAllTrucksUseCase getAllTrucksUseCase;
    private final GetTruckByIdUseCase getTruckByIdUseCase;
    private final SaveTruckUseCase saveTruckUseCase;
    private final UpdateTruckUseCase updateTruckUseCase;
    private final UpdateTruckStatusUseCase updateTruckStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los camiones de la empresa; SECRETARY/SUPER_ADMIN ven todos, "
            + "PARTNER solo los de propiedad suya")
    public TrucksResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "truckId") Pageable pageable) {
        return getAllTrucksUseCase.execute(companyId, pageable);
    }

    @GetMapping("/{truckId}")
    @Operation(summary = "Consulta un camión por id dentro de la empresa; "
            + "PARTNER solo camiones de su propiedad")
    public TruckResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del camión") @PathVariable Long truckId) {
        return getTruckByIdUseCase.execute(companyId, truckId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra un camión con socio propietario obligatorio "
            + "(SUPER_ADMIN o SECRETARY de esa empresa)")
    public TruckResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody TruckRequest request) {
        return saveTruckUseCase.execute(companyId, request);
    }

    @PutMapping("/{truckId}")
    @Operation(summary = "Actualiza los datos escalares de un camión sin cambiar de socio "
            + "(SUPER_ADMIN o SECRETARY de esa empresa)")
    public TruckResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del camión") @PathVariable Long truckId,
            @Valid @RequestBody TruckUpdateRequest request) {
        return updateTruckUseCase.execute(companyId, truckId, request);
    }

    @PutMapping("/{truckId}/status")
    @Operation(summary = "Cambia el estado del camión (ACTIVE/MAINTENANCE/INACTIVE) "
            + "(SUPER_ADMIN o SECRETARY de esa empresa)")
    public TruckResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del camión") @PathVariable Long truckId,
            @Valid @RequestBody TruckStatusRequest request) {
        return updateTruckStatusUseCase.execute(companyId, truckId, request);
    }
}
