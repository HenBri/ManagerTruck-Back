package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.DriverStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.BadRequestException;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationService;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SaveDriverUseCase {

    private final DriverService driverService;
    private final PartnerService partnerService;
    private final CompanyService companyService;
    private final CompanyUserService companyUserService;
    private final UserService userService;
    private final LicenseValidationService licenseValidationService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DriverResponse execute(Long companyId, DriverRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear conductores en una empresa inactiva");
        }
        licenseValidationService.validate(request.getLicenseCategory(), request.getLicenseExpiration());
        if (request.getUserId() != null) {
            validateUserLink(companyId, request.getUserId());
            if (driverService.existsByCompanyIdAndUserId(companyId, request.getUserId())) {
                throw new ConflictException(
                        "La cuenta ya está vinculada como conductor de esta empresa; no se permiten cuentas duplicadas");
            }
        }
        if (request.getPersonPartnerId() != null) {
            validatePersonPartner(companyId, request.getPersonPartnerId());
        }
        Driver driver = new Driver();
        driver.setCompanyId(companyId);
        driver.setUserId(request.getUserId());
        driver.setPersonPartnerId(request.getPersonPartnerId());
        driver.setDriverName(request.getDriverName().trim());
        driver.setDriverCi(request.getDriverCi());
        driver.setDriverPhone(request.getDriverPhone());
        driver.setDriverLicense(request.getDriverLicense().trim());
        driver.setLicenseCategory(licenseValidationService.normalizeCategory(request.getLicenseCategory()));
        driver.setLicenseExpiration(request.getLicenseExpiration());
        driver.setDriverState(DriverStatus.ACTIVE);
        Driver saved = driverService.save(driver);
        return DriverResponse.from(saved);
    }

    private void validateUserLink(Long companyId, Long userId) {
        CompanyUser membership = companyUserService.findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "La cuenta no es miembro de esta empresa"));
        if (membership.getMembershipStatus() != MembershipStatus.ACTIVE) {
            throw new ConflictException("No se puede vincular una cuenta con membresía inactiva");
        }
        User user = userService.findById(userId);
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new ConflictException("No se puede vincular una cuenta inactiva");
        }
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
