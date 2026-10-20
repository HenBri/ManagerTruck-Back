package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.CompanyUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompanyUserServiceImpl implements CompanyUserService {

    private final CompanyUserRepository companyUserRepository;

    @Override
    public CompanyUser save(CompanyUser companyUser) {
        return companyUserRepository.save(companyUser);
    }

    @Override
    public CompanyUser findById(CompanyUserId id) {
        return companyUserRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membresía no encontrada para la empresa " + id.getCompanyId() + " y usuario " + id.getUserId()));
    }

    @Override
    public Optional<CompanyUser> findByCompanyIdAndUserId(Long companyId, Long userId) {
        return companyUserRepository.findByCompanyIdAndUserId(companyId, userId);
    }

    @Override
    public boolean existsByCompanyIdAndUserId(Long companyId, Long userId) {
        return companyUserRepository.findByCompanyIdAndUserId(companyId, userId).isPresent();
    }

    @Override
    public Page<CompanyUser> findByCompanyId(Long companyId, Pageable pageable) {
        return companyUserRepository.findByCompanyId(companyId, pageable);
    }

    @Override
    public Page<CompanyUser> findActiveByCompanyId(Long companyId, Pageable pageable) {
        return companyUserRepository.findActiveByCompanyId(companyId, pageable);
    }

    @Override
    public List<CompanyUser> findByUserId(Long userId) {
        return companyUserRepository.findByUserId(userId);
    }
}
