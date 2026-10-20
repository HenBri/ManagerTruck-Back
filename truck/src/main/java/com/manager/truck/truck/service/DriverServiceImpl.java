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
    public Driver findByCompanyIdAndDriverId(Long companyId, Long driverId) {
        return driverRepository.findByCompanyIdAndDriverId(companyId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor no encontrado con el id " + driverId + " en la empresa " + companyId));
    }

    @Override
    public Driver findByCompanyIdAndDriverIdForUpdate(Long companyId, Long driverId) {
        return driverRepository.findByCompanyIdAndDriverIdForUpdate(companyId, driverId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conductor no encontrado con el id " + driverId + " en la empresa " + companyId));
    }

    @Override
    public Optional<Driver> findByCompanyIdAndUserId(Long companyId, Long userId) {
        return driverRepository.findByCompanyIdAndUserId(companyId, userId);
    }

    @Override
    public boolean existsByCompanyIdAndUserId(Long companyId, Long userId) {
        return driverRepository.existsByCompanyIdAndUserId(companyId, userId);
    }

    @Override
    public boolean existsByCompanyIdAndPersonPartnerId(Long companyId, Long personPartnerId) {
        return driverRepository.existsByCompanyIdAndPersonPartnerId(companyId, personPartnerId);
    }

    @Override
    public Page<Driver> findByCompanyId(Long companyId, Pageable pageable) {
        return driverRepository.findByCompanyId(companyId, pageable);
    }
}
