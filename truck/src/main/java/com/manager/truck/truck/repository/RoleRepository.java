package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Roles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Roles, Long> {
}
