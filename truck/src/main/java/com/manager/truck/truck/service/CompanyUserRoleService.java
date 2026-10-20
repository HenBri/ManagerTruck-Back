package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.CompanyUserRoleId;

import java.util.List;

public interface CompanyUserRoleService {

    CompanyUserRole save(CompanyUserRole companyUserRole);

    List<CompanyUserRole> saveAll(List<CompanyUserRole> companyUserRoles);

    CompanyUserRole findById(CompanyUserRoleId id);

    boolean existsById(CompanyUserRoleId id);

    List<CompanyUserRole> findByCompanyIdAndUserId(Long companyId, Long userId);

    boolean existsByCompanyIdAndUserIdAndRoleId(Long companyId, Long userId, Integer roleId);
}
