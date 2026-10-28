package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetUserByIdUseCase {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public UserResponse execute(Long userId) {
        CurrentUser currentUser = currentUserProvider.get();
        if (!currentUser.userId().equals(userId) && !currentUserProvider.isAdmin()) {
            throw new ForbiddenOperationException("Solo puede consultar su propia cuenta o ser administrador");
        }
        return UserResponse.from(userService.findById(userId));
    }
}
