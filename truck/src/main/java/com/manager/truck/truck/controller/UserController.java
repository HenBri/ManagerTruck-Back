package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.UserRequest;
import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.usecase.user.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    @Autowired
    private UserSaveUseCase userSaveUseCase;

    @Autowired
    private GetUserByIdUseCase getUserByIdUseCase;
    @Autowired
    private GetAllUsersUseCase getAllUsersUseCase;
    @Autowired
    private DeleteUserById deleteUserById;
    @Autowired
    private UpdateUserUseCase updateUserUseCase;




    @PostMapping("/user")
    public UserResponse save(@RequestBody UserRequest userRequest){
       return userSaveUseCase.execute(userRequest);
    }
    @GetMapping("GetUserById/{userId}")
    public UserResponse getUserById(@PathVariable Long userId){
        return getUserByIdUseCase.execute(userId);
    }

    @GetMapping("GetAll")
    public UsersResponse getAllUsers(){
        return getAllUsersUseCase.execute();
    }
    @PutMapping("/user/{id}")
    private UserResponse update(@PathVariable Long id,@RequestBody UserRequest userRequest){
        return updateUserUseCase.execute(id, userRequest);
    }
    @DeleteMapping("/{userId}")
    private void delete(@PathVariable Long userId){
        deleteUserById.execute(userId);
    }
}
