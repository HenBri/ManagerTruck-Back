package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.request.JourneyStatusRequest;
import com.manager.truck.truck.domain.dto.request.JourneyUpdateRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.usecase.journey.GetAllJourneysUseCase;
import com.manager.truck.truck.usecase.journey.GetJourneyByIdUseCase;
import com.manager.truck.truck.usecase.journey.SaveJourneyUseCase;
import com.manager.truck.truck.usecase.journey.UpdateJourneyStatusUseCase;
import com.manager.truck.truck.usecase.journey.UpdateJourneyUseCase;
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
@RequestMapping("/api/companies/{companyId}/journeys")
@RequiredArgsConstructor
@Tag(name = "Journeys",
        description = "Viajes A -> B de un solo camión/conductor; modalidad PARTNER (asignación) o "
                + "AUXILIARY (proveedor externo). SECRETARY/SUPER_ADMIN administran; PARTNER consulta "
                + "viajes de sus camiones; DRIVER consulta e inicia/completa sus viajes asignados")
public class JourneyController {

    private final GetAllJourneysUseCase getAllJourneysUseCase;
    private final GetJourneyByIdUseCase getJourneyByIdUseCase;
    private final SaveJourneyUseCase saveJourneyUseCase;
    private final UpdateJourneyUseCase updateJourneyUseCase;
    private final UpdateJourneyStatusUseCase updateJourneyStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los viajes visibles para el rol autenticado con filtros opcionales "
            + "por contenedor, estado y modalidad")
    public JourneysResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Filtra por contenedor transportado") @RequestParam(required = false)
            Long containerId,
            @Parameter(description = "Filtra por estado del viaje") @RequestParam(required = false)
            JourneyState state,
            @Parameter(description = "Filtra por modalidad de ejecución") @RequestParam(required = false)
            JourneyExecutionType executionType,
            @PageableDefault(size = 20, sort = "journeyId") Pageable pageable) {
        return getAllJourneysUseCase.execute(companyId, containerId, state, executionType, pageable);
    }

    @GetMapping("/{journeyId}")
    @Operation(summary = "Consulta un viaje por id dentro de la empresa según el alcance del rol")
    public JourneyResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del viaje") @PathVariable Long journeyId) {
        return getJourneyByIdUseCase.execute(companyId, journeyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un viaje PLANNED (SUPER_ADMIN o SECRETARY); "
            + "PARTNER requiere assignmentId vigente, AUXILIARY requiere proveedor, placa, conductor y motivo")
    public JourneyResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody JourneyRequest request) {
        return saveJourneyUseCase.execute(companyId, request);
    }

    @PutMapping("/{journeyId}")
    @Operation(summary = "Actualiza los datos escalares de un viaje PLANNED sin cambiar modalidad ni estado")
    public JourneyResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del viaje") @PathVariable Long journeyId,
            @Valid @RequestBody JourneyUpdateRequest request) {
        return updateJourneyUseCase.execute(companyId, journeyId, request);
    }

    @PutMapping("/{journeyId}/status")
    @Operation(summary = "Cambia el estado del viaje con transiciones estrictas "
            + "(PLANNED->IN_PROGRESS->COMPLETED; CANCELLED desde PLANNED o IN_PROGRESS). "
            + "DRIVER solo puede iniciar o completar sus viajes PARTNER asignados")
    public JourneyResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del viaje") @PathVariable Long journeyId,
            @Valid @RequestBody JourneyStatusRequest request) {
        return updateJourneyStatusUseCase.execute(companyId, journeyId, request);
    }
}
