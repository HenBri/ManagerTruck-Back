package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {
    private final CompanyRepository companyRepository;

    @Override public Company save(Company company) { return companyRepository.save(company); }
    @Override public Company update(Company company) { return companyRepository.save(company); }
    @Override public Company findById(Long id) {
        return companyRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con id " + id));
    }
    @Override public Optional<Company> findOptionalById(Long id) { return companyRepository.findById(id); }
    @Override public Page<Company> findAll(Pageable pageable) { return companyRepository.findAll(pageable); }
}
