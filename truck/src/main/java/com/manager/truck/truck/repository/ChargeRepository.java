package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Charge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeRepository extends JpaRepository<Charge, Long> {
}
