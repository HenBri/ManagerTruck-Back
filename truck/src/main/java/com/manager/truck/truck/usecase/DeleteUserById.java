package com.manager.truck.truck.usecase;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteUserById {
    @Autowired
    private UserService userService;

    public void execute(Long id){
        User user= userService.findById(id);
        user.setState(false);

        userService.update(user);
    }
}
