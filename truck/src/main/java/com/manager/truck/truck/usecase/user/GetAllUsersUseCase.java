package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllUsersUseCase {
    @Autowired
    private UserService userService;

    public UsersResponse execute(){
        List<User> users = userService.findAll();
        return new UsersResponse(users);
    }

}
