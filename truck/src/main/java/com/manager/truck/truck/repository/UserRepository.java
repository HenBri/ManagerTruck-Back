package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
