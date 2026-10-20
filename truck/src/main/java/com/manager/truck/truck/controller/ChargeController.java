package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.ChargeRequest;
import com.manager.truck.truck.domain.dto.request.ChargeStatusRequest;
import com.manager.truck.truck.domain.dto.request.ChargeUpdateRequest;
import com.manager.truck.truck.domain.dto.response.ChargeResponse;
import com.manager.truck.truck.domain.dto.response.ChargesResponse;
import com.manager.truck.truck.domain.enums.ChargeState;
import com.manager.truck.truck.usecase.charge.GetAllChargesUseCase;
import com.manager.truck.truck.usecase.charge.GetChargeByIdUseCase;
import com.manager.truck.truck.usecase.charge.SaveChargeUseCase;
import com.manager.truck.truck.usecase.charge.UpdateChargeStatusUseCase;
import com.manager.truck.truck.usecase.charge.UpdateChargeUseCase;
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
@RequestMapping("/api/companies/{companyId}/charges")
@RequiredArgsConstructor
@Tag(name = "Charges", description = "Cargas de un contrato; el cliente se deriva del contrato. "
        + "Solo SUPER_ADMIN y SECRETARY administran (PARTNER/DRIVER sin acceso)")
public class ChargeController {

    private final GetAllChargesUseCase getAllChargesUseCase;
    private final GetChargeByIdUseCase getChargeByIdUseCase;
    private final SaveChargeUseCase saveChargeUseCase;
    private final UpdateChargeUseCase updateChargeUseCase;
    private final UpdateChargeStatusUseCase updateChargeStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista las cargas de una empresa con filtros opcionales por contrato y estado")
    public ChargesResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Filtra por contrato dueño de la carga") @RequestParam(required = false) Long contractId,
            @Parameter(description = "Filtra por estado de la carga") @RequestParam(required = false) ChargeState state,
            @PageableDefault(size = 20, sort = "chargeId") Pageable pageable) {
        return getAllChargesUseCase.execute(companyId, contractId, state, pageable);
    }

    @GetMapping("/{chargeId}")
    @Operation(summary = "Consulta una carga por id dentro de la empresa")
    public ChargeResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la carga") @PathVariable Long chargeId) {
        return getChargeByIdUseCase.execute(companyId, chargeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una carga para un contrato ACTIVO de la empresa "
            + "(SUPER_ADMIN o SECRETARY); el cliente se deriva del contrato")
    public ChargeResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody ChargeRequest request) {
        return saveChargeUseCase.execute(companyId, request);
    }

    @PutMapping("/{chargeId}")
    @Operation(summary = "Actualiza los datos escalares de una carga sin cambiar contrato ni estado")
    public ChargeResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la carga") @PathVariable Long chargeId,
            @Valid @RequestBody ChargeUpdateRequest request) {
        return updateChargeUseCase.execute(companyId, chargeId, request);
    }

    @PutMapping("/{chargeId}/status")
    @Operation(summary = "Cambia el estado de la carga con transiciones estrictas "
            + "(PENDING→IN_TRANSIT|CANCELLED, IN_TRANSIT→DELIVERED|CANCELLED)")
    public ChargeResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la carga") @PathVariable Long chargeId,
            @Valid @RequestBody ChargeStatusRequest request) {
        return updateChargeStatusUseCase.execute(companyId, chargeId, request);
    }
}
