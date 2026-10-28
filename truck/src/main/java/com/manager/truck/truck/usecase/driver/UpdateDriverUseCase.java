package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.domain.dto.request.DriverUpdateRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationService;
import com.manager.truck.truck.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateDriverUseCase {

    private final DriverService driverService;
    private final PersonService personService;
    private final LicenseValidationService licenseValidationService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DriverResponse execute(Long driverId, DriverUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Driver driver = driverService.findById(driverId);
        licenseValidationService.validate(request.getLicenseCategory(), request.getLicenseExpiration());

        String license = request.getDriverLicense().trim();
        if (!license.equalsIgnoreCase(driver.getDriverLicense())
                && driverService.existsByDriverLicenseAndDriverIdNot(license, driverId)) {
            throw new ConflictException("Ya existe un conductor con la licencia " + license);
        }

        driver.setDriverLicense(license);
        driver.setLicenseCategory(licenseValidationService.normalizeCategory(request.getLicenseCategory()));
        driver.setLicenseExpiration(request.getLicenseExpiration());

        Person person = personService.findById(driver.getDriverId());
        if (request.getFirstName() != null) person.setFirstName(request.getFirstName());
        if (request.getLastName() != null) person.setLastName(request.getLastName());
        if (request.getMaternalLastName() != null) person.setMaternalLastName(request.getMaternalLastName());
        if (request.getDocumentNumber() != null) person.setDocumentNumber(request.getDocumentNumber());
        if (request.getPhone() != null) person.setPhone(request.getPhone());
        if (request.getEmail() != null) {
            String email = request.getEmail().trim();
            if (!email.equalsIgnoreCase(person.getEmail()) && personService.existsByEmailAndPersonIdNot(email, person.getPersonId())) {
                throw new ConflictException("Ya existe una persona registrada con el email " + email);
            }
            person.setEmail(email);
        }
        personService.update(person);

        Driver saved = driverService.update(driver);
        saved.setPerson(person);
        return DriverResponse.from(saved);
    }
}
