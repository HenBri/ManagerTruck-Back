package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Partner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PartnerService {

    Partner save(Partner partner);

    Partner findByCompanyIdAndPartnerId(Long companyId, Long partnerId);

    Optional<Partner> findOptionalByCompanyIdAndPartnerId(Long companyId, Long partnerId);

    Optional<Partner> findByCompanyIdAndUserId(Long companyId, Long userId);

    boolean existsByCompanyIdAndUserId(Long companyId, Long userId);

    Page<Partner> findByCompanyId(Long companyId, Pageable pageable);
}
