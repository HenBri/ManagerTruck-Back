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
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByUserEmail(email);
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public List<String> findPlatformRoleNames(Long userId) {
        return userRepository.findPlatformRoleNames(userId);
    }

    @Override
    public List<String> findAllCompanyRoleNames(Long userId) {
        return userRepository.findAllCompanyRoleNames(userId);
    }

    @Override
    public List<String> findCompanyRoleNames(Long userId, Long companyId) {
        return userRepository.findCompanyRoleNames(userId, companyId);
    }

    @Override
    public boolean isActiveMember(Long userId, Long companyId) {
        return userRepository.countActiveMembership(userId, companyId) > 0;
    }

    @Override
    public Page<User> findCompanyMembers(Long companyId, Pageable pageable) {
        return userRepository.findCompanyMembers(companyId, pageable);
    }
}
