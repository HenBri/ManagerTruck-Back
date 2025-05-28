package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetUserByIdUseCase {
    @Autowired
    private UserService userService;

    public UserResponse execute(Long id){
        User builduser = userService.findById(id);
        return new UserResponse(builduser);
    }
}
