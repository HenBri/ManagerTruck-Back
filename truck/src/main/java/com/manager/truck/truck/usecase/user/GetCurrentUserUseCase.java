package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCurrentUserUseCase {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public UserResponse execute() {
        CurrentUser currentUser = currentUserProvider.get();
        return UserResponse.from(userService.findById(currentUser.userId()));
    }
}
