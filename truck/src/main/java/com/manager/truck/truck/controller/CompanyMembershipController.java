package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.CompanyMembershipRequest;
import com.manager.truck.truck.domain.dto.request.CompanyMembershipStatusRequest;
import com.manager.truck.truck.domain.dto.request.CompanyUserRolesRequest;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipsResponse;
import com.manager.truck.truck.domain.dto.response.CompanyUserRolesResponse;
import com.manager.truck.truck.usecase.companyuser.GetCompanyMembershipsUseCase;
import com.manager.truck.truck.usecase.companyuser.SaveCompanyUserUseCase;
import com.manager.truck.truck.usecase.companyuser.UpdateCompanyMembershipStatusUseCase;
import com.manager.truck.truck.usecase.companyuserrole.AssignCompanyUserRolesUseCase;
import com.manager.truck.truck.usecase.companyuserrole.GetCompanyUserRolesUseCase;
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
@RequestMapping("/api/companies/{companyId}/memberships")
@RequiredArgsConstructor
@Tag(name = "Company memberships", description = "Membresías y roles por empresa (SUPER_ADMIN o SECRETARY de la empresa)")
public class CompanyMembershipController {

    private final GetCompanyMembershipsUseCase getCompanyMembershipsUseCase;
    private final SaveCompanyUserUseCase saveCompanyUserUseCase;
    private final UpdateCompanyMembershipStatusUseCase updateCompanyMembershipStatusUseCase;
    private final GetCompanyUserRolesUseCase getCompanyUserRolesUseCase;
    private final AssignCompanyUserRolesUseCase assignCompanyUserRolesUseCase;

    @GetMapping
    @Operation(summary = "Lista las membresías de una empresa paginadas (SUPER_ADMIN o SECRETARY de esa empresa)")
    public CompanyMembershipsResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "id.userId") Pageable pageable) {
        return getCompanyMembershipsUseCase.execute(companyId, pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una membresía en la empresa a partir de un correo (SUPER_ADMIN o SECRETARY de esa empresa)")
    public CompanyMembershipResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody CompanyMembershipRequest request) {
        return saveCompanyUserUseCase.execute(companyId, request);
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "Activa o desactiva la membresía de un usuario (SUPER_ADMIN o SECRETARY de esa empresa)")
    public CompanyMembershipResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la cuenta") @PathVariable Long userId,
            @Valid @RequestBody CompanyMembershipStatusRequest request) {
        return updateCompanyMembershipStatusUseCase.execute(companyId, userId, request);
    }

    @GetMapping("/{userId}/roles")
    @Operation(summary = "Consulta los roles de un miembro en la empresa (SUPER_ADMIN, SECRETARY de esa empresa o el propio usuario)")
    public CompanyUserRolesResponse getRoles(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la cuenta") @PathVariable Long userId) {
        return getCompanyUserRolesUseCase.execute(companyId, userId);
    }

    @PostMapping("/{userId}/roles")
    @Operation(summary = "Asigna uno o varios roles COMPANY a un miembro activo (SUPER_ADMIN o SECRETARY de esa empresa); nunca acepta SUPER_ADMIN")
    public CompanyUserRolesResponse assignRoles(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador de la cuenta") @PathVariable Long userId,
            @Valid @RequestBody CompanyUserRolesRequest request) {
        return assignCompanyUserRolesUseCase.execute(companyId, userId, request);
    }
}
