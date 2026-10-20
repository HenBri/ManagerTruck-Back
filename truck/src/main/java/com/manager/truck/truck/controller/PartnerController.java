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
@RequestMapping("/api/companies/{companyId}/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Socios/propietarios de camiones por empresa; "
        + "SECRETARY administra, PARTNER y DRIVER consultan solo sus datos")
public class PartnerController {

    private final GetAllPartnersUseCase getAllPartnersUseCase;
    private final GetPartnerByIdUseCase getPartnerByIdUseCase;
    private final SavePartnerUseCase savePartnerUseCase;
    private final UpdatePartnerUseCase updatePartnerUseCase;
    private final UpdatePartnerStatusUseCase updatePartnerStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los socios de una empresa; SECRETARY/SUPER_ADMIN ven todos, "
            + "PARTNER/DRIVER solo su propio perfil")
    public PartnersResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "partnerId") Pageable pageable) {
        return getAllPartnersUseCase.execute(companyId, pageable);
    }

    @GetMapping("/{partnerId}")
    @Operation(summary = "Consulta un socio por id dentro de la empresa; "
            + "no administradores solo su propio perfil")
    public PartnerResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del socio") @PathVariable Long partnerId) {
        return getPartnerByIdUseCase.execute(companyId, partnerId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un socio en la empresa (SUPER_ADMIN o SECRETARY de esa empresa)")
    public PartnerResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody PartnerRequest request) {
        return savePartnerUseCase.execute(companyId, request);
    }

    @PutMapping("/{partnerId}")
    @Operation(summary = "Actualiza los datos escalares de un socio (SUPER_ADMIN o SECRETARY de esa empresa)")
    public PartnerResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del socio") @PathVariable Long partnerId,
            @Valid @RequestBody PartnerUpdateRequest request) {
        return updatePartnerUseCase.execute(companyId, partnerId, request);
    }

    @PutMapping("/{partnerId}/status")
    @Operation(summary = "Activa o desactiva un socio (SUPER_ADMIN o SECRETARY de esa empresa)")
    public PartnerResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del socio") @PathVariable Long partnerId,
            @Valid @RequestBody PartnerStatusRequest request) {
        return updatePartnerStatusUseCase.execute(companyId, partnerId, request);
    }
}
