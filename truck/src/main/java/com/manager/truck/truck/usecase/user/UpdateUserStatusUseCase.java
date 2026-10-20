package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserStatusRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserStatusUseCase {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public UserResponse execute(Long userId, UserStatusRequest request) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede cambiar el estado de una cuenta");
        }
        CurrentUser currentUser = currentUserProvider.get();
        if (currentUser.userId().equals(userId) && request.getStatus() == UserStatus.INACTIVE) {
            throw new ConflictException("No puede desactivar su propia cuenta");
        }
        User user = userService.findById(userId);
        user.setUserStatus(request.getStatus());
        return UserResponse.from(userService.update(user));
    }
}
