package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRequest;

import java.util.List;

public interface UserService {
    User save(User user);
    User findById(Long id);
    User update(User user);

    List<User> findAll();

}

