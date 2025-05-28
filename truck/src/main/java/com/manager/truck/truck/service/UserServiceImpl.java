package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.UserRequest;
import com.manager.truck.truck.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    UserRepository userRepository;

    public User save(User user){
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

}
