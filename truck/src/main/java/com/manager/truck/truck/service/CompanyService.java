package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CompanyService {

    Company save(Company company);

    Company findById(Long companyId);

    Company update(Company company);

    Page<Company> findAll(Pageable pageable);
}
