package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {
}
