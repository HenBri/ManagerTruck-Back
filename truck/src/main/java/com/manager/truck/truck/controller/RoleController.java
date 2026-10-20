package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.RoleRequest;
import com.manager.truck.truck.domain.dto.request.RoleUpdateRequest;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.domain.dto.response.RolesResponse;
import com.manager.truck.truck.usecase.role.GetAllRolesUseCase;
import com.manager.truck.truck.usecase.role.GetRoleByIdUseCase;
import com.manager.truck.truck.usecase.role.SaveRoleUseCase;
import com.manager.truck.truck.usecase.role.UpdateRoleUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Catálogo global de roles PLATFORM/COMPANY")
public class RoleController {

    private final GetAllRolesUseCase getAllRolesUseCase;
    private final GetRoleByIdUseCase getRoleByIdUseCase;
    private final SaveRoleUseCase saveRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;

    @GetMapping
    @Operation(summary = "Lista el catálogo de roles (autenticado)")
    public RolesResponse getAll() {
        return getAllRolesUseCase.execute();
    }

    @GetMapping("/{roleId}")
    @Operation(summary = "Consulta un rol por id (autenticado)")
    public RoleResponse getById(
            @Parameter(description = "Identificador del rol") @PathVariable("roleId") Integer roleId) {
        return getRoleByIdUseCase.execute(roleId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un rol (SUPER_ADMIN); no asigna roles a usuarios")
    public RoleResponse create(@Valid @RequestBody RoleRequest request) {
        return saveRoleUseCase.execute(request);
    }

    @PutMapping("/{roleId}")
    @Operation(summary = "Actualiza nombre/descripción de un rol sin cambiar su scope (SUPER_ADMIN)")
    public RoleResponse update(
            @Parameter(description = "Identificador del rol") @PathVariable("roleId") Integer roleId,
            @Valid @RequestBody RoleUpdateRequest request) {
        return updateRoleUseCase.execute(roleId, request);
    }
}
