package com.manager.truck.truck.usecase;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateUserUseCase {
    @Autowired
    private UserService userService;

    public UserResponse execute(Long id, UserRequest userRequest){
        User userbuild =userBuild(id, userRequest);
        userService.update(userbuild);
        return userResponseBuild(userbuild);

    }

    private User userBuild(Long id, UserRequest userRequest){
        User user = userService.findById(id);

        user.setName(userRequest.getName());
        user.setLastname(userRequest.getLastname());
        user.setEmail(userRequest.getEmail());
        user.setPhone(userRequest.getPhone());
        user.setCi(userRequest.getCi());
        user.setRole(userRequest.getRole());
        return user;
    }

    private UserResponse userResponseBuild(User user){
        return new UserResponse(user);
    }
}
