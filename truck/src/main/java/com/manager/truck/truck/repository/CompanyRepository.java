package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
