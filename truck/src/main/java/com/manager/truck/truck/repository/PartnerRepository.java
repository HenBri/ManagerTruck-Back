package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    @Query("SELECT p FROM Partner p WHERE p.companyId = :companyId AND p.partnerId = :partnerId")
    Optional<Partner> findByCompanyIdAndPartnerId(@Param("companyId") Long companyId,
                                                  @Param("partnerId") Long partnerId);

    @Query("SELECT p FROM Partner p WHERE p.companyId = :companyId")
    Page<Partner> findByCompanyId(@Param("companyId") Long companyId, Pageable pageable);

    @Query("SELECT p FROM Partner p WHERE p.companyId = :companyId AND p.userId = :userId")
    Optional<Partner> findByCompanyIdAndUserId(@Param("companyId") Long companyId,
                                               @Param("userId") Long userId);

    @Query("SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END FROM Partner p "
            + "WHERE p.companyId = :companyId AND p.userId = :userId")
    boolean existsByCompanyIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);
}
