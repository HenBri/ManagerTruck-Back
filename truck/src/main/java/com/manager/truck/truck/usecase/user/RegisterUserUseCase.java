package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRegisterRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserResponse execute(UserRegisterRequest request) {
        if (userService.existsByEmail(request.getEmail())) {
            throw new ConflictException("El correo ya está registrado: " + request.getEmail());
        }
        User user = new User();
        user.setUserEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setUserName(request.getName());
        user.setUserLastname(request.getLastname());
        user.setUserCi(request.getCi());
        user.setUserPhone(request.getPhone());
        user.setUserStatus(UserStatus.ACTIVE);
        User saved;
        try {
            saved = userService.save(user);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException("El correo ya está registrado: " + request.getEmail());
        }
        return UserResponse.from(userService.findById(saved.getUserId()));
    }
}
