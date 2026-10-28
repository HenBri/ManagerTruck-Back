package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.dto.request.PartnerStatusRequest;
import com.manager.truck.truck.domain.dto.request.PartnerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.dto.response.PartnersResponse;
import com.manager.truck.truck.usecase.partner.GetAllPartnersUseCase;
import com.manager.truck.truck.usecase.partner.GetPartnerByIdUseCase;
import com.manager.truck.truck.usecase.partner.SavePartnerUseCase;
import com.manager.truck.truck.usecase.partner.UpdatePartnerStatusUseCase;
import com.manager.truck.truck.usecase.partner.UpdatePartnerUseCase;
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
@RequestMapping("/api/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Socios/propietarios de camiones")
public class PartnerController {

    private final GetAllPartnersUseCase getAllPartnersUseCase;
    private final GetPartnerByIdUseCase getPartnerByIdUseCase;
    private final SavePartnerUseCase savePartnerUseCase;
    private final UpdatePartnerUseCase updatePartnerUseCase;
    private final UpdatePartnerStatusUseCase updatePartnerStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los socios; ADMIN/SECRETARY ven todos, PARTNER solo su propio perfil")
    public PartnersResponse getAll(@PageableDefault(size = 20, sort = "partnerId") Pageable pageable) {
        return getAllPartnersUseCase.execute(pageable);
    }

    @GetMapping("/{partnerId}")
    @Operation(summary = "Consulta un socio por id")
    public PartnerResponse getById(
            @Parameter(description = "Identificador del socio") @PathVariable Long partnerId) {
        return getPartnerByIdUseCase.execute(partnerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un socio")
    public PartnerResponse create(@Valid @RequestBody PartnerRequest request) {
        return savePartnerUseCase.execute(request);
    }

    @PutMapping("/{partnerId}")
    @Operation(summary = "Actualiza los datos de un socio")
    public PartnerResponse update(
            @Parameter(description = "Identificador del socio") @PathVariable Long partnerId,
            @Valid @RequestBody PartnerUpdateRequest request) {
        return updatePartnerUseCase.execute(partnerId, request);
    }

    @PutMapping("/{partnerId}/status")
    @Operation(summary = "Activa o desactiva un socio")
    public PartnerResponse updateStatus(
            @Parameter(description = "Identificador del socio") @PathVariable Long partnerId,
            @Valid @RequestBody PartnerStatusRequest request) {
        return updatePartnerStatusUseCase.execute(partnerId, request);
    }
}
