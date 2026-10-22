package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.request.TruckDriverAssignmentRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyAccountProvisioningService;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.usecase.truckdriverassignment.AssignTruckDriverUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SaveDriverUseCase {

    private final DriverService driverService;
    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CompanyAccountProvisioningService accountProvisioningService;
    private final LicenseValidationService licenseValidationService;
    private final CurrentUserProvider currentUserProvider;
    private final AssignTruckDriverUseCase assignTruckDriverUseCase;

    @Transactional
    public DriverResponse execute(Long companyId, DriverRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear conductores en una empresa inactiva");
        }
        licenseValidationService.validate(request.getLicenseCategory(), request.getLicenseExpiration());
        User user = accountProvisioningService.provision(companyId, request.getEmail(), request.getPassword(),
                request.getName(), request.getLastname(), request.getCi(), request.getPhone(), "DRIVER");
        if (request.getPersonPartnerId() != null) {
            validatePersonPartner(companyId, request.getPersonPartnerId());
        }
        Driver driver = new Driver();
        driver.setCompanyId(companyId);
        driver.setUserId(user.getUserId());
        driver.setPersonPartnerId(request.getPersonPartnerId());
        driver.setDriverName(user.getUserName() + " " + user.getUserLastname());
        driver.setDriverCi(user.getUserCi());
        driver.setDriverPhone(user.getUserPhone());
        driver.setDriverLicense(request.getDriverLicense().trim());
        driver.setLicenseCategory(licenseValidationService.normalizeCategory(request.getLicenseCategory()));
        driver.setLicenseExpiration(request.getLicenseExpiration());
        driver.setDriverState(DriverStatus.ACTIVE);
        Driver saved = driverService.save(driver);
        if (request.getTruckId() != null) {
            TruckDriverAssignmentRequest assignment = new TruckDriverAssignmentRequest();
            assignment.setTruckId(request.getTruckId());
            assignment.setDriverId(saved.getDriverId());
            assignment.setAssignedFrom(LocalDateTime.now());
            assignTruckDriverUseCase.execute(companyId, assignment);
        }
        return DriverResponse.from(saved);
    }

    private void validatePersonPartner(Long companyId, Long personPartnerId) {
        Partner partner = partnerService.findByCompanyIdAndPartnerId(companyId, personPartnerId);
        if (partner.getPartnerType() != PartnerType.PERSON) {
            throw new BadRequestException(
                    "person_partner_id debe referenciar un socio de tipo PERSON de la misma empresa");
        }
        if (driverService.existsByCompanyIdAndPersonPartnerId(companyId, personPartnerId)) {
            throw new ConflictException(
                    "Ese socio ya está vinculado como conductor de esta empresa");
        }
    }
}
