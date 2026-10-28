package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.request.DriverStatusRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDriverStatusUseCase {

    private final DriverService driverService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DriverResponse execute(Long driverId, DriverStatusRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Driver driver = driverService.findById(driverId);
        String current = driver.getState();
        String target = request.getState();
        if (target.equalsIgnoreCase(current)) {
            throw new ConflictException("El conductor ya se encuentra en el estado " + current);
        }
        driver.setState(target);
        Driver saved = driverService.update(driver);
        return DriverResponse.from(saved);
    }
}
