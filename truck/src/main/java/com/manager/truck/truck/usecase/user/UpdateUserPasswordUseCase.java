package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserPasswordRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserPasswordUseCase {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse execute(Long userId, UserPasswordRequest request) {
        CurrentUser currentUser = currentUserProvider.get();
        if (!currentUser.userId().equals(userId)) {
            throw new ForbiddenOperationException("La contraseña solo puede cambiarla el dueño de la cuenta");
        }
        User user = userService.findById(userId);
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPasswordHash())) {
            throw new ForbiddenOperationException("La contraseña actual no coincide");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        return UserResponse.from(userService.update(user));
    }
}
