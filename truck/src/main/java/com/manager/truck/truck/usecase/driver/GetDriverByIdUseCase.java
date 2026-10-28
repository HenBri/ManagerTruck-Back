package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDriverByIdUseCase {

    private final DriverService driverService;
    private final CurrentUserProvider currentUserProvider;

    public DriverResponse execute(Long driverId) {
        Driver driver = driverService.findById(driverId);
        if (!currentUserProvider.isAdmin() && !currentUserProvider.isSecretary()) {
            Long currentUserId = currentUserProvider.get().userId();
            if (!currentUserId.equals(driver.getDriverId())) {
                throw new ForbiddenOperationException("Solo puedes consultar tu propia información de conductor");
            }
        }
        return DriverResponse.from(driver);
    }
}
