package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    boolean existsByRoleName(String roleName);

    Optional<Role> findByRoleName(String roleName);
}
