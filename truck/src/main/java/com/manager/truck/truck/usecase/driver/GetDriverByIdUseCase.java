package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetDriverByIdUseCase {

    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public DriverResponse execute(Long companyId, Long driverId) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Driver driver = driverService.findByCompanyIdAndDriverId(companyId, driverId);
        if (!hasFullAccess(companyId)) {
            Long userId = currentUserProvider.get().userId();
            if (!userId.equals(driver.getUserId())) {
                throw new ForbiddenOperationException("Solo puedes consultar tu propia información de conductor");
            }
        }
        return DriverResponse.from(driver);
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
