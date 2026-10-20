package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CompanyUserService {

    CompanyUser save(CompanyUser companyUser);

    CompanyUser findById(CompanyUserId id);

    Optional<CompanyUser> findByCompanyIdAndUserId(Long companyId, Long userId);

    boolean existsByCompanyIdAndUserId(Long companyId, Long userId);

    Page<CompanyUser> findByCompanyId(Long companyId, Pageable pageable);

    Page<CompanyUser> findActiveByCompanyId(Long companyId, Pageable pageable);

    List<CompanyUser> findByUserId(Long userId);
}
