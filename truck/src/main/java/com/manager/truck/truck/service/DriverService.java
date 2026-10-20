package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DriverService {

    Driver save(Driver driver);

    Driver findByCompanyIdAndDriverId(Long companyId, Long driverId);

    Driver findByCompanyIdAndDriverIdForUpdate(Long companyId, Long driverId);

    Optional<Driver> findByCompanyIdAndUserId(Long companyId, Long userId);

    boolean existsByCompanyIdAndUserId(Long companyId, Long userId);

    boolean existsByCompanyIdAndPersonPartnerId(Long companyId, Long personPartnerId);

    Page<Driver> findByCompanyId(Long companyId, Pageable pageable);
}
