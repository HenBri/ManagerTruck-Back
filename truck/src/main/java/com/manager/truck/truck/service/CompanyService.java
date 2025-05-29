package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Company;

import java.util.List;

public interface CompanyService {
    Company save(Company company);
    Company findById(Long id);
    Company update(Company company);
    void delete(Long id);
    List<Company> findAll();
}
