package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.CompanyUserRoleId;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.CompanyUserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyUserRoleServiceImpl implements CompanyUserRoleService {

    private final CompanyUserRoleRepository companyUserRoleRepository;

    @Override
    public CompanyUserRole save(CompanyUserRole companyUserRole) {
        return companyUserRoleRepository.save(companyUserRole);
    }

    @Override
    public List<CompanyUserRole> saveAll(List<CompanyUserRole> companyUserRoles) {
        return companyUserRoleRepository.saveAll(companyUserRoles);
    }

    @Override
    public CompanyUserRole findById(CompanyUserRoleId id) {
        return companyUserRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Asignación de rol no encontrada para la empresa " + id.getCompanyId()));
    }

    @Override
    public boolean existsById(CompanyUserRoleId id) {
        return companyUserRoleRepository.existsById(id);
    }

    @Override
    public List<CompanyUserRole> findByCompanyIdAndUserId(Long companyId, Long userId) {
        return companyUserRoleRepository.findByCompanyIdAndUserId(companyId, userId);
    }

    @Override
    public boolean existsByCompanyIdAndUserIdAndRoleId(Long companyId, Long userId, Integer roleId) {
        return companyUserRoleRepository.findByCompanyIdAndUserId(companyId, userId).stream()
                .anyMatch(assignment -> roleId.equals(assignment.getId().getRoleId()));
    }
}
