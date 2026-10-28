package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.enums.PartnerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PartnerService {

    Partner save(Partner partner);

    Partner update(Partner partner);

    Partner findById(Long partnerId);

    Optional<Partner> findOptionalById(Long partnerId);

    Page<Partner> findAll(Pageable pageable);

    Page<Partner> findByType(PartnerType partnerType, Pageable pageable);
}
