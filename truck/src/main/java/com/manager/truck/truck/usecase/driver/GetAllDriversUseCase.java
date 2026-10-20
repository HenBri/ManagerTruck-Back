package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.dto.response.DriversResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllDriversUseCase {

    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public DriversResponse execute(Long companyId, Pageable pageable) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Page<Driver> page;
        if (hasFullAccess(companyId)) {
            page = driverService.findByCompanyId(companyId, pageable);
        } else {
            Long userId = currentUserProvider.get().userId();
            Optional<Driver> ownDriver = driverService.findByCompanyIdAndUserId(companyId, userId);
            page = ownDriver
                    .map(driver -> (Page<Driver>) new PageImpl<>(List.of(driver), pageable, 1))
                    .orElseGet(() -> Page.empty(pageable));
        }
        Page<DriverResponse> content = page.map(DriverResponse::from);
        return new DriversResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
