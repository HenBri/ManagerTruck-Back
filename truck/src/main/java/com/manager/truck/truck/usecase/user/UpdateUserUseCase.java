package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserUpdateRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateUserUseCase {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public UserResponse execute(Long userId, UserUpdateRequest request) {
        CurrentUser currentUser = currentUserProvider.get();
        if (!currentUser.userId().equals(userId) && !currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo puede modificar su propia cuenta o ser SUPER_ADMIN");
        }
        User user = userService.findById(userId);
        user.setUserName(request.getName());
        user.setUserLastname(request.getLastname());
        user.setUserCi(request.getCi());
        user.setUserPhone(request.getPhone());
        return UserResponse.from(userService.update(user));
    }
}
