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
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Drivers", description = "Conductores de la empresa. ADMIN/SECRETARY administran, DRIVER/PARTNER consultan")
public class DriverController {

    private final GetAllDriversUseCase getAllDriversUseCase;
    private final GetDriverByIdUseCase getDriverByIdUseCase;
    private final SaveDriverUseCase saveDriverUseCase;
    private final UpdateDriverUseCase updateDriverUseCase;
    private final UpdateDriverStatusUseCase updateDriverStatusUseCase;

    @GetMapping
    @Operation(summary = "Lista los conductores; ADMIN/SECRETARY ven todos, DRIVER solo su propio registro")
    public DriversResponse getAll(@PageableDefault(size = 20, sort = "driverId") Pageable pageable) {
        return getAllDriversUseCase.execute(pageable);
    }

    @GetMapping("/{driverId}")
    @Operation(summary = "Consulta un conductor por id")
    public DriverResponse getById(
            @Parameter(description = "Identificador del conductor") @PathVariable Long driverId) {
        return getDriverByIdUseCase.execute(driverId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea un conductor")
    public DriverResponse create(@Valid @RequestBody DriverRequest request) {
        return saveDriverUseCase.execute(request);
    }

    @PutMapping("/{driverId}")
    @Operation(summary = "Actualiza los datos de un conductor")
    public DriverResponse update(
            @Parameter(description = "Identificador del conductor") @PathVariable Long driverId,
            @Valid @RequestBody DriverUpdateRequest request) {
        return updateDriverUseCase.execute(driverId, request);
    }

    @PutMapping("/{driverId}/status")
    @Operation(summary = "Activa o desactiva un conductor")
    public DriverResponse updateStatus(
            @Parameter(description = "Identificador del conductor") @PathVariable Long driverId,
            @Valid @RequestBody DriverStatusRequest request) {
        return updateDriverStatusUseCase.execute(driverId, request);
    }
}
