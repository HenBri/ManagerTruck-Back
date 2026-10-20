package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, CompanyUserId> {

    @Query("SELECT cu FROM CompanyUser cu WHERE cu.id.companyId = :companyId AND cu.id.userId = :userId")
    Optional<CompanyUser> findByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);

    @Query("SELECT cu FROM CompanyUser cu WHERE cu.id.companyId = :companyId")
    Page<CompanyUser> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT cu FROM CompanyUser cu "
            + "WHERE cu.id.companyId = :companyId "
            + "AND cu.membershipStatus = com.manager.truck.truck.domain.enums.MembershipStatus.ACTIVE")
    Page<CompanyUser> findActiveByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT cu FROM CompanyUser cu WHERE cu.id.userId = :userId")
    List<CompanyUser> findByUserId(@Param("userId") Long userId);
}
