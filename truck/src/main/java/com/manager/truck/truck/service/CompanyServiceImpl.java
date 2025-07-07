package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.repository.CompanyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService{

    @Autowired
    private CompanyRepository companyRepository;


    @Override
    public Company save(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public Company findById(Long id) {
        return companyRepository.getById(id);
    }

    @Override
    public Company update(Company company) {
        return companyRepository.save(company);
    }

    @Override
    public void delete(Long id) {
    }

    @Override
    public List<Company> findAll() {
        return companyRepository.findAll();
    }
}
