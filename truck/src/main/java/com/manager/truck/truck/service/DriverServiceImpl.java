package com.manager.truck.truck.service;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriverServiceImpl implements DriverService {

    private final DriverRepository driverRepository;

    @Override
    public Driver save(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public Driver update(Driver driver) {
        return driverRepository.save(driver);
    }

    @Override
    public Driver findById(Long driverId) {
        return driverRepository.findActiveById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Conductor no encontrado con el id " + driverId));
    }

    @Override
    public Optional<Driver> findOptionalById(Long driverId) {
        return driverRepository.findActiveById(driverId);
    }

    @Override
    public boolean existsByDriverLicense(String driverLicense) {
        return driverRepository.existsByDriverLicense(driverLicense);
    }

    @Override
    public boolean existsByDriverLicenseAndDriverIdNot(String driverLicense, Long driverId) {
        return driverRepository.existsByDriverLicenseAndDriverIdNot(driverLicense, driverId);
    }

    @Override
    public Page<Driver> findAll(Pageable pageable) {
        return driverRepository.findAllActive(pageable);
    }
}
