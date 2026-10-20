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
@RequestMapping("/api/companies/{companyId}/assignments")
@RequiredArgsConstructor
@Tag(name = "TruckDriverAssignments", description = "Asignación de conductores a camiones de socios con "
        + "historial; SECRETARY administra, PARTNER consulta las de sus camiones, DRIVER las suyas")
public class TruckDriverAssignmentController {

    private final AssignTruckDriverUseCase assignTruckDriverUseCase;
    private final GetAllTruckDriverAssignmentsUseCase getAllTruckDriverAssignmentsUseCase;
    private final GetTruckDriverAssignmentByIdUseCase getTruckDriverAssignmentByIdUseCase;
    private final EndTruckDriverAssignmentUseCase endTruckDriverAssignmentUseCase;

    @GetMapping
    @Operation(summary = "Historial de asignaciones de la empresa; SECRETARY/SUPER_ADMIN ven todas "
            + "(filtros opcionales truckId/driverId), PARTNER las de sus camiones, DRIVER las suyas")
    public TruckDriverAssignmentsResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Filtro opcional por camión") @RequestParam(required = false) Long truckId,
            @Parameter(description = "Filtro opcional por conductor") @RequestParam(required = false) Long driverId,
            @PageableDefault(size = 20, sort = "assignmentId") Pageable pageable) {
        return getAllTruckDriverAssignmentsUseCase.execute(companyId, truckId, driverId, pageable);
    }

    @GetMapping("/{assignmentId}")
    @Operation(summary = "Consulta una asignación por id dentro de la empresa; "
            + "PARTNER solo asignaciones de sus camiones, DRIVER solo las suyas")
    public TruckDriverAssignmentResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la asignación") @PathVariable Long assignmentId) {
        return getTruckDriverAssignmentByIdUseCase.execute(companyId, assignmentId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Asigna un conductor a un camión de un socio con vigencia; no modifica la propiedad "
            + "del camión ni permite solapes (SUPER_ADMIN o SECRETARY de esa empresa)")
    public TruckDriverAssignmentResponse assign(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody TruckDriverAssignmentRequest request) {
        return assignTruckDriverUseCase.execute(companyId, request);
    }

    @PutMapping("/{assignmentId}/end")
    @Operation(summary = "Finaliza una asignación abierta conservando el historial "
            + "(SUPER_ADMIN o SECRETARY de esa empresa)")
    public TruckDriverAssignmentResponse end(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la asignación") @PathVariable Long assignmentId,
            @Valid @RequestBody TruckDriverAssignmentEndRequest request) {
        return endTruckDriverAssignmentUseCase.execute(companyId, assignmentId, request);
    }
}
