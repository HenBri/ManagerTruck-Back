package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContractRepository extends JpaRepository<Contract, Long> {
}
