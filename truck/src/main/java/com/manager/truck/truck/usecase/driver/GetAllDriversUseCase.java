package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.dto.response.DriversResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.DriverService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllDriversUseCase {

    private final DriverService driverService;
    private final CurrentUserProvider currentUserProvider;

    public DriversResponse execute(Pageable pageable) {
        Page<Driver> page;
        if (currentUserProvider.isAdmin() || currentUserProvider.isSecretary()) {
            page = driverService.findAll(pageable);
        } else {
            Long currentUserId = currentUserProvider.get().userId();
            page = driverService.findOptionalById(currentUserId)
                    .map(driver -> (Page<Driver>) new PageImpl<>(List.of(driver), pageable, 1))
                    .orElseGet(() -> Page.empty(pageable));
        }
        Page<DriverResponse> content = page.map(DriverResponse::from);
        return new DriversResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }
}
