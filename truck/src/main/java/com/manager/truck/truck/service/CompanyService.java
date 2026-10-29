package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface CompanyService {
    Company save(Company company);
    Company update(Company company);
    Company findById(Long companyId);
    Optional<Company> findOptionalById(Long companyId);
    Page<Company> findAll(Pageable pageable);
}
