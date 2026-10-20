package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.request.DriverStatusRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDriverStatusUseCase {

    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DriverResponse execute(Long companyId, Long driverId, DriverStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Driver driver = driverService.findByCompanyIdAndDriverId(companyId, driverId);
        if (driver.getDriverState() == request.getStatus()) {
            throw new ConflictException("El conductor ya se encuentra en el estado " + request.getStatus());
        }
        driver.setDriverState(request.getStatus());
        Driver saved = driverService.save(driver);
        return DriverResponse.from(saved);
    }
}
