package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.request.DriverStatusRequest;
import com.manager.truck.truck.domain.dto.request.DriverUpdateRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.dto.response.DriversResponse;
import com.manager.truck.truck.usecase.driver.GetAllDriversUseCase;
import com.manager.truck.truck.usecase.driver.GetDriverByIdUseCase;
import com.manager.truck.truck.usecase.driver.SaveDriverUseCase;
import com.manager.truck.truck.usecase.driver.UpdateDriverStatusUseCase;
import com.manager.truck.truck.usecase.driver.UpdateDriverUseCase;
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
@RequestMapping("/api/companies/{companyId}/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Conductores por empresa; "
        + "SECRETARY administra, DRIVER/PARTNER consultan solo su información")
public class DriverController {

    private final GetAllDriversUseCase getAllDriversUseCase;
    private final GetDriverByIdUseCase getDriverByIdUseCase;
    private final SaveDriverUseCase saveDriverUseCase;
    private final UpdateDriverUseCase updateDriverUseCase;
    private final UpdateDriverStatusUseCase updateDriverStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los conductores de una empresa; SECRETARY/SUPER_ADMIN ven todos, "
            + "DRIVER/PARTNER solo su propio registro")
    public DriversResponse getAll(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @PageableDefault(size = 20, sort = "driverId") Pageable pageable) {
        return getAllDriversUseCase.execute(companyId, pageable);
    }

    @GetMapping("/{driverId}")
    @Operation(summary = "Consulta un conductor por id dentro de la empresa; "
            + "no administradores solo su propio registro")
    public DriverResponse getById(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del conductor") @PathVariable Long driverId) {
        return getDriverByIdUseCase.execute(companyId, driverId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un conductor en la empresa (SUPER_ADMIN o SECRETARY de esa empresa); "
            + "valida licencia, duplicidad de cuenta y vínculo socio-persona")
    public DriverResponse create(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Valid @RequestBody DriverRequest request) {
        return saveDriverUseCase.execute(companyId, request);
    }

    @PutMapping("/{driverId}")
    @Operation(summary = "Actualiza los datos escalares de un conductor incluyendo licencia "
            + "(SUPER_ADMIN o SECRETARY de esa empresa)")
    public DriverResponse update(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del conductor") @PathVariable Long driverId,
            @Valid @RequestBody DriverUpdateRequest request) {
        return updateDriverUseCase.execute(companyId, driverId, request);
    }

    @PutMapping("/{driverId}/status")
    @Operation(summary = "Activa o desactiva un conductor (SUPER_ADMIN o SECRETARY de esa empresa)")
    public DriverResponse updateStatus(
            @Parameter(description = "Identificador de la empresa (tenant de la ruta)") @PathVariable Long companyId,
            @Parameter(description = "Identificador del conductor") @PathVariable Long driverId,
            @Valid @RequestBody DriverStatusRequest request) {
        return updateDriverStatusUseCase.execute(companyId, driverId, request);
    }
}
