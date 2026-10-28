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
@RequestMapping("/api/charges")
@RequiredArgsConstructor
@Tag(name = "Charges", description = "Cargas de un contrato. Solo ADMIN y SECRETARY administran")
public class ChargeController {

    private final GetAllChargesUseCase getAllChargesUseCase;
    private final GetChargeByIdUseCase getChargeByIdUseCase;
    private final SaveChargeUseCase saveChargeUseCase;
    private final UpdateChargeUseCase updateChargeUseCase;
    private final UpdateChargeStatusUseCase updateChargeStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista las cargas con filtros opcionales por contrato")
    public ChargesResponse getAll(
            @Parameter(description = "Filtra por contrato dueño de la carga") @RequestParam(required = false) Long contractId,
            @PageableDefault(size = 20, sort = "chargeId") Pageable pageable) {
        return getAllChargesUseCase.execute(contractId, pageable);
    }

    @GetMapping("/{chargeId}")
    @Operation(summary = "Consulta una carga por id")
    public ChargeResponse getById(
            @Parameter(description = "Identificador de la carga") @PathVariable Long chargeId) {
        return getChargeByIdUseCase.execute(chargeId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una carga para un contrato")
    public ChargeResponse create(@Valid @RequestBody ChargeRequest request) {
        return saveChargeUseCase.execute(request);
    }

    @PutMapping("/{chargeId}")
    @Operation(summary = "Actualiza los datos de una carga sin cambiar su estado")
    public ChargeResponse update(
            @Parameter(description = "Identificador de la carga") @PathVariable Long chargeId,
            @Valid @RequestBody ChargeUpdateRequest request) {
        return updateChargeUseCase.execute(chargeId, request);
    }

    @PutMapping("/{chargeId}/status")
    @Operation(summary = "Cambia el estado de la carga")
    public ChargeResponse updateStatus(
            @Parameter(description = "Identificador de la carga") @PathVariable Long chargeId,
            @Valid @RequestBody ChargeStatusRequest request) {
        return updateChargeStatusUseCase.execute(chargeId, request);
    }
}
