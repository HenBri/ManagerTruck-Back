package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.CompanyUserRole;
import com.manager.truck.truck.domain.CompanyUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyUserRoleRepository extends JpaRepository<CompanyUserRole, CompanyUserRoleId> {

    @Query("SELECT cur FROM CompanyUserRole cur WHERE cur.id.companyId = :companyId AND cur.id.userId = :userId")
    List<CompanyUserRole> findByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);
}
