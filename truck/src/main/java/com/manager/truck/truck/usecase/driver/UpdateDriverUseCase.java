package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.request.DriverUpdateRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDriverUseCase {

    private final DriverService driverService;
    private final CompanyService companyService;
    private final LicenseValidationService licenseValidationService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DriverResponse execute(Long companyId, Long driverId, DriverUpdateRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Driver driver = driverService.findByCompanyIdAndDriverId(companyId, driverId);
        licenseValidationService.validate(request.getLicenseCategory(), request.getLicenseExpiration());
        driver.setDriverName(request.getDriverName().trim());
        driver.setDriverCi(request.getDriverCi());
        driver.setDriverPhone(request.getDriverPhone());
        driver.setDriverLicense(request.getDriverLicense().trim());
        driver.setLicenseCategory(licenseValidationService.normalizeCategory(request.getLicenseCategory()));
        driver.setLicenseExpiration(request.getLicenseExpiration());
        Driver saved = driverService.save(driver);
        return DriverResponse.from(saved);
    }
}
