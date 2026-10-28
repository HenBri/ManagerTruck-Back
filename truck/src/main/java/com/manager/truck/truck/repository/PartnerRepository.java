package com.manager.truck.truck.repository;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.enums.PartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartnerRepository extends JpaRepository<Partner, Long> {

    @Query("SELECT p FROM Partner p LEFT JOIN FETCH p.person WHERE p.partnerId = :partnerId AND p.isDelete = false")
    Optional<Partner> findActiveById(@Param("partnerId") Long partnerId);

    @Query("SELECT p FROM Partner p LEFT JOIN FETCH p.person WHERE p.isDelete = false")
    Page<Partner> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Partner p LEFT JOIN FETCH p.person WHERE p.partnerType = :type AND p.isDelete = false")
    Page<Partner> findByType(@Param("type") PartnerType type, Pageable pageable);
}
