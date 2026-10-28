package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findActiveById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));
    }

    @Override
    public Optional<User> findOptionalById(Long userId) {
        return userRepository.findActiveById(userId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByPersonEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByPersonEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAllActive(pageable);
    }

    @Override
    public List<String> findRoleNames(Long userId) {
        return userRepository.findRoleNames(userId);
    }
}
