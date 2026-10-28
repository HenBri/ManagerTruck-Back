package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface DriverService {

    Driver save(Driver driver);

    Driver update(Driver driver);

    Driver findById(Long driverId);

    Optional<Driver> findOptionalById(Long driverId);

    boolean existsByDriverLicense(String driverLicense);

    boolean existsByDriverLicenseAndDriverIdNot(String driverLicense, Long driverId);

    Page<Driver> findAll(Pageable pageable);
}
