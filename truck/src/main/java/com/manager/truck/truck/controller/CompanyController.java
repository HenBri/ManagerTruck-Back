package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.request.CompanyStatusRequest;
import com.manager.truck.truck.domain.dto.response.CompaniesResponse;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.usecase.company.GetAllCompaniesUseCase;
import com.manager.truck.truck.usecase.company.GetCompanyByIdUseCase;
import com.manager.truck.truck.usecase.company.SaveCompanyUseCase;
import com.manager.truck.truck.usecase.company.UpdateCompanyStatusUseCase;
import com.manager.truck.truck.usecase.company.UpdateCompanyUseCase;
import com.manager.truck.truck.usecase.user.GetCompanyUsersUseCase;
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
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Administración de empresas (tenant) del SaaS")
public class CompanyController {

    private final SaveCompanyUseCase saveCompanyUseCase;
    private final GetAllCompaniesUseCase getAllCompaniesUseCase;
    private final GetCompanyByIdUseCase getCompanyByIdUseCase;
    private final UpdateCompanyUseCase updateCompanyUseCase;
    private final UpdateCompanyStatusUseCase updateCompanyStatusUseCase;
    private final GetCompanyUsersUseCase getCompanyUsersUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una empresa (SUPER_ADMIN)")
    public CompanyResponse create(@Valid @RequestBody CompanyRequest request) {
        return saveCompanyUseCase.execute(request);
    }

    @GetMapping
    @Operation(summary = "Lista empresas paginadas (SUPER_ADMIN)")
    public CompaniesResponse getAll(
            @PageableDefault(size = 20, sort = "companyId") Pageable pageable) {
        return getAllCompaniesUseCase.execute(pageable);
    }

    @GetMapping("/{companyId}")
    @Operation(summary = "Consulta una empresa por id (SUPER_ADMIN o miembro activo)")
    public CompanyResponse getById(
            @Parameter(description = "Identificador de la empresa") @PathVariable("companyId") Long companyId) {
        return getCompanyByIdUseCase.execute(companyId);
    }

    @PutMapping("/{companyId}")
    @Operation(summary = "Actualiza datos de una empresa sin cambiar su estado (SUPER_ADMIN)")
    public CompanyResponse update(
            @Parameter(description = "Identificador de la empresa") @PathVariable("companyId") Long companyId,
            @Valid @RequestBody CompanyRequest request) {
        return updateCompanyUseCase.execute(companyId, request);
    }

    @PutMapping("/{companyId}/status")
    @Operation(summary = "Activa o desactiva una empresa, soft delete (SUPER_ADMIN)")
    public CompanyResponse updateStatus(
            @Parameter(description = "Identificador de la empresa") @PathVariable("companyId") Long companyId,
            @Valid @RequestBody CompanyStatusRequest request) {
        return updateCompanyStatusUseCase.execute(companyId, request);
    }

    @GetMapping("/{companyId}/users")
    @Operation(summary = "Lista los miembros de una empresa (SUPER_ADMIN o SECRETARY de esa empresa)")
    public UsersResponse getUsers(
            @Parameter(description = "Identificador de la empresa") @PathVariable("companyId") Long companyId,
            @PageableDefault(size = 20, sort = "userId") Pageable pageable) {
        return getCompanyUsersUseCase.execute(companyId, pageable);
    }
}
