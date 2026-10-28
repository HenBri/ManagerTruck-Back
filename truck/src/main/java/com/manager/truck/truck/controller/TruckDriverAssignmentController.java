package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentEndRequest;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentResponse;
import com.manager.truck.truck.domain.dto.response.TruckDriverAssignmentsResponse;
import com.manager.truck.truck.usecase.truckdriverassignment.AssignTruckDriverUseCase;
import com.manager.truck.truck.usecase.truckdriverassignment.EndTruckDriverAssignmentUseCase;
import com.manager.truck.truck.usecase.truckdriverassignment.GetAllTruckDriverAssignmentsUseCase;
import com.manager.truck.truck.usecase.truckdriverassignment.GetTruckDriverAssignmentByIdUseCase;
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
@RequestMapping("/api/assignments")
@RequiredArgsConstructor
@Tag(name = "TruckDriverAssignments", description = "Asignación de conductores a camiones")
public class TruckDriverAssignmentController {

    private final AssignTruckDriverUseCase assignTruckDriverUseCase;
    private final GetAllTruckDriverAssignmentsUseCase getAllTruckDriverAssignmentsUseCase;
    private final GetTruckDriverAssignmentByIdUseCase getTruckDriverAssignmentByIdUseCase;
    private final EndTruckDriverAssignmentUseCase endTruckDriverAssignmentUseCase;

    @GetMapping
    @Operation(summary = "Historial de asignaciones")
    public TruckDriverAssignmentsResponse getAll(
            @Parameter(description = "Filtro opcional por camión") @RequestParam(required = false) Long truckId,
            @Parameter(description = "Filtro opcional por conductor") @RequestParam(required = false) Long driverId,
            @PageableDefault(size = 20, sort = "assignmentId") Pageable pageable) {
        return getAllTruckDriverAssignmentsUseCase.execute(truckId, driverId, pageable);
    }

    @GetMapping("/{assignmentId}")
    @Operation(summary = "Consulta una asignación por id")
    public TruckDriverAssignmentResponse getById(
            @Parameter(description = "Identificador de la asignación") @PathVariable Long assignmentId) {
        return getTruckDriverAssignmentByIdUseCase.execute(assignmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Asigna un conductor a un camión")
    public TruckDriverAssignmentResponse assign(@Valid @RequestBody TruckDriverAssignmentRequest request) {
        return assignTruckDriverUseCase.execute(request);
    }

    @PutMapping("/{assignmentId}/end")
    @Operation(summary = "Finaliza una asignación")
    public TruckDriverAssignmentResponse end(
            @Parameter(description = "Identificador de la asignación") @PathVariable Long assignmentId,
            @Valid @RequestBody TruckDriverAssignmentEndRequest request) {
        return endTruckDriverAssignmentUseCase.execute(assignmentId, request);
    }
}
