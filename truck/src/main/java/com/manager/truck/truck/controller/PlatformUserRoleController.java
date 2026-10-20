package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.response.PlatformUserRolesResponse;
import com.manager.truck.truck.usecase.platformuserrole.GetPlatformUserRolesUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
@Tag(name = "Platform roles", description = "Auditoría de roles PLATFORM (solo lectura; el alta de SUPER_ADMIN es un procedimiento manual seguro)")
public class PlatformUserRoleController {

    private final GetPlatformUserRolesUseCase getPlatformUserRolesUseCase;

    @GetMapping("/user-roles")
    @Operation(summary = "Lista los grants de roles de plataforma (SUPER_ADMIN); no existe endpoint de escritura")
    public PlatformUserRolesResponse getPlatformUserRoles() {
        return getPlatformUserRolesUseCase.execute();
    }
}
