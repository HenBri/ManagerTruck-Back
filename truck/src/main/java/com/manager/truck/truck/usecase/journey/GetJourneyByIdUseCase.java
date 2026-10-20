package com.manager.truck.truck.usecase.journey;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Journey;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.dto.response.JourneyResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.JourneyService;
import com.manager.truck.truck.service.PartnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetJourneyByIdUseCase {

    private final JourneyService journeyService;
    private final PartnerService partnerService;
    private final DriverService driverService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional(readOnly = true)
    public JourneyResponse execute(Long companyId, Long journeyId) {
        currentUserProvider.requireCompanyAccess(companyId);
        companyService.findById(companyId);
        Journey journey;
        if (hasFullAccess(companyId)) {
            journey = journeyService.findByCompanyIdAndJourneyId(companyId, journeyId);
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.PARTNER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Partner> ownPartner = partnerService.findByCompanyIdAndUserId(companyId, userId);
            journey = ownPartner
                    .flatMap(partner -> journeyService.findOptionalByCompanyIdAndJourneyIdAndOwnerPartnerId(
                            companyId, journeyId, partner.getPartnerId()))
                    .orElseThrow(() -> notFound(companyId, journeyId));
        } else if (currentUserProvider.hasCompanyRole(companyId, SecurityRoles.DRIVER)) {
            Long userId = currentUserProvider.get().userId();
            Optional<Driver> ownDriver = driverService.findByCompanyIdAndUserId(companyId, userId);
            journey = ownDriver
                    .flatMap(driver -> journeyService.findOptionalByCompanyIdAndJourneyIdAndDriverId(
                            companyId, journeyId, driver.getDriverId()))
                    .orElseThrow(() -> notFound(companyId, journeyId));
        } else {
            throw new ForbiddenOperationException("No tienes acceso a los viajes de esta empresa");
        }
        return JourneyResponse.from(journey);
    }

    private ResourceNotFoundException notFound(Long companyId, Long journeyId) {
        return new ResourceNotFoundException(
                "Viaje no encontrado con el id " + journeyId + " en la empresa " + companyId);
    }

    private boolean hasFullAccess(Long companyId) {
        return currentUserProvider.isSuperAdmin()
                || currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY);
    }
}
