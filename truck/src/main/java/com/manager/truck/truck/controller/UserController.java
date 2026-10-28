package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.UserPasswordRequest;
import com.manager.truck.truck.domain.dto.request.UserRegisterRequest;
import com.manager.truck.truck.domain.dto.request.UserStatusRequest;
import com.manager.truck.truck.domain.dto.request.UserUpdateRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.usecase.user.GetAllUsersUseCase;
import com.manager.truck.truck.usecase.user.GetCurrentUserUseCase;
import com.manager.truck.truck.usecase.user.GetUserByIdUseCase;
import com.manager.truck.truck.usecase.user.RegisterUserUseCase;
import com.manager.truck.truck.usecase.user.UpdateUserPasswordUseCase;
import com.manager.truck.truck.usecase.user.UpdateUserStatusUseCase;
import com.manager.truck.truck.usecase.user.UpdateUserUseCase;
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
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Gestión de usuarios")
public class UserController {

    private final RegisterUserUseCase registerUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final GetUserByIdUseCase getUserByIdUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final UpdateUserPasswordUseCase updateUserPasswordUseCase;
    private final UpdateUserStatusUseCase updateUserStatusUseCase;
    private final GetAllUsersUseCase getAllUsersUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registra una cuenta nueva con contraseña hasheada")
    public UserResponse register(@Valid @RequestBody UserRegisterRequest request) {
        return registerUserUseCase.execute(request);
    }

    @PostMapping("/admin")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crea una cuenta desde el panel de administración")
    public UserResponse registerByAdmin(@Valid @RequestBody UserRegisterRequest request) {
        return registerUserUseCase.execute(request);
    }

    @GetMapping("/me")
    @Operation(summary = "Devuelve la cuenta autenticada")
    public UserResponse getCurrentUser() {
        return getCurrentUserUseCase.execute();
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Consulta una cuenta por id (propia o ADMIN)")
    public UserResponse getById(
            @Parameter(description = "Identificador de la cuenta") @PathVariable("userId") Long userId) {
        return getUserByIdUseCase.execute(userId);
    }

    @PutMapping("/{userId}")
    @Operation(summary = "Actualiza datos personales de una cuenta (propia o ADMIN)")
    public UserResponse update(
            @Parameter(description = "Identificador de la cuenta") @PathVariable("userId") Long userId,
            @Valid @RequestBody UserUpdateRequest request) {
        return updateUserUseCase.execute(userId, request);
    }

    @PutMapping("/{userId}/password")
    @Operation(summary = "Cambia la contraseña de la propia cuenta verificando la actual")
    public UserResponse updatePassword(
            @Parameter(description = "Identificador de la cuenta") @PathVariable("userId") Long userId,
            @Valid @RequestBody UserPasswordRequest request) {
        return updateUserPasswordUseCase.execute(userId, request);
    }

    @PutMapping("/{userId}/status")
    @Operation(summary = "Activa o desactiva una cuenta (ADMIN)")
    public UserResponse updateStatus(
            @Parameter(description = "Identificador de la cuenta") @PathVariable("userId") Long userId,
            @Valid @RequestBody UserStatusRequest request) {
        return updateUserStatusUseCase.execute(userId, request);
    }

    @GetMapping
    @Operation(summary = "Lista todas las cuentas registradas (ADMIN)")
    public UsersResponse getAll(@PageableDefault(size = 20, sort = "userId") Pageable pageable) {
        return getAllUsersUseCase.execute(pageable);
    }
}
