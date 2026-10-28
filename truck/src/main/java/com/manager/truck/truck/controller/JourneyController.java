package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.JourneyRequest;
import com.manager.truck.truck.domain.dto.request.JourneyStatusRequest;
import com.manager.truck.truck.domain.dto.request.JourneyUpdateRequest;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
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
@RequestMapping("/api/journeys")
@RequiredArgsConstructor
@Tag(name = "Journeys", description = "Viajes asociados a fletes")
public class JourneyController {

    private final GetAllJourneysUseCase getAllJourneysUseCase;
    private final GetJourneyByIdUseCase getJourneyByIdUseCase;
    private final SaveJourneyUseCase saveJourneyUseCase;
    private final UpdateJourneyUseCase updateJourneyUseCase;
    private final UpdateJourneyStatusUseCase updateJourneyStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los viajes con filtros opcionales por flete, asignación y estado")
    public JourneysResponse getAll(
            @Parameter(description = "Filtra por flete") @RequestParam(required = false) Long freightId,
            @Parameter(description = "Filtra por asignación") @RequestParam(required = false) Long assignmentId,
            @Parameter(description = "Filtra por estado del viaje") @RequestParam(required = false) JourneyState state,
            @PageableDefault(size = 20, sort = "journeyId") Pageable pageable) {
        return getAllJourneysUseCase.execute(freightId, assignmentId, state, pageable);
    }

    @GetMapping("/{journeyId}")
    @Operation(summary = "Consulta un viaje por id")
    public JourneyResponse getById(
            @Parameter(description = "Identificador del viaje") @PathVariable Long journeyId) {
        return getJourneyByIdUseCase.execute(journeyId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un viaje")
    public JourneyResponse create(@Valid @RequestBody JourneyRequest request) {
        return saveJourneyUseCase.execute(request);
    }

    @PutMapping("/{journeyId}")
    @Operation(summary = "Actualiza los datos de un viaje sin cambiar su estado")
    public JourneyResponse update(
            @Parameter(description = "Identificador del viaje") @PathVariable Long journeyId,
            @Valid @RequestBody JourneyUpdateRequest request) {
        return updateJourneyUseCase.execute(journeyId, request);
    }

    @PutMapping("/{journeyId}/status")
    @Operation(summary = "Cambia el estado del viaje")
    public JourneyResponse updateStatus(
            @Parameter(description = "Identificador del viaje") @PathVariable Long journeyId,
            @Valid @RequestBody JourneyStatusRequest request) {
        return updateJourneyStatusUseCase.execute(journeyId, request);
    }
}
