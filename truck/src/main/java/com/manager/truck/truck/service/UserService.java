package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface UserService {

    User save(User user);

    User update(User user);

    User findById(Long userId);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<User> findAll(Pageable pageable);

    List<String> findPlatformRoleNames(Long userId);

    List<String> findAllCompanyRoleNames(Long userId);

    List<String> findCompanyRoleNames(Long userId, Long companyId);

    boolean isActiveMember(Long userId, Long companyId);

    Page<User> findCompanyMembers(Long companyId, Pageable pageable);
}
