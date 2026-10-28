package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllUsersUseCase {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public UsersResponse execute(Pageable pageable) {
        currentUserProvider.requireAdmin();
        Page<UserResponse> page = userService.findAll(pageable).map(UserResponse::from);
        List<UserResponse> users = page.getContent();
        return new UsersResponse(users, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
