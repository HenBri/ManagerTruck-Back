package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.domain.dto.response.JourneysResponse;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import com.manager.truck.truck.domain.enums.JourneyState;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetAllJourneysUseCase {

    private final JourneyService journeyService;
    private final PartnerService partnerService;
    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public JourneysResponse execute(Long companyId, Long containerId, JourneyState state,
                                    JourneyExecutionType executionType, Pageable pageable) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Page<Journey> page;
        if (hasFullAccess(companyId)) {
            page = findWithOptionalFilters(companyId, containerId, state, executionType, pageable);
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.PARTNER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
            page = ownPartner
                    .map(partner -> journeyService.findByCompanyIdAndOwnerPartnerId(companyId,
                            partner.getPartnerId(), containerId, state, executionType, pageable))
                    .orElseGet(() -> Page.empty(pageable));
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.DRIVER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Driver> ownDriver = driverService.findByCompanyIdAndUserId(companyId, userId);
            page = ownDriver
                    .map(driver -> journeyService.findByCompanyIdAndDriverId(companyId,
                            driver.getDriverId(), containerId, state, executionType, pageable))
                    .orElseGet(() -> Page.empty(pageable));
        } else {
            throw new ForbiddenOperationException("No tienes acceso a los viajes de esta empresa");
        }
        Page<JourneyResponse> content = page.map(JourneyResponse::from);
        return new JourneysResponse(content.getContent(), content.getNumber(), content.getSize(),
                content.getTotalElements(), content.getTotalPages());
    }

    private Page<Journey> findWithOptionalFilters(Long companyId, Long containerId, JourneyState state,
                                                  JourneyExecutionType executionType, Pageable pageable) {
        if (containerId != null && state != null && executionType != null) {
            return journeyService.findByCompanyIdAndContainerIdAndJourneyStateAndExecutionType(
                    companyId, containerId, state, executionType, pageable);
        }
        if (containerId != null && state != null) {
            return journeyService.findByCompanyIdAndContainerIdAndJourneyState(
                    companyId, containerId, state, pageable);
        }
        if (containerId != null && executionType != null) {
            return journeyService.findByCompanyIdAndContainerIdAndExecutionType(
                    companyId, containerId, executionType, pageable);
        }
        if (state != null && executionType != null) {
            return journeyService.findByCompanyIdAndJourneyStateAndExecutionType(
                    companyId, state, executionType, pageable);
        }
        if (containerId != null) {
            return journeyService.findByCompanyIdAndContainerId(companyId, containerId, pageable);
        }
        if (state != null) {
            return journeyService.findByCompanyIdAndJourneyState(companyId, state, pageable);
        }
        if (executionType != null) {
            return journeyService.findByCompanyIdAndExecutionType(companyId, executionType, pageable);
        }
        return journeyService.findByCompanyId(companyId, pageable);
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
