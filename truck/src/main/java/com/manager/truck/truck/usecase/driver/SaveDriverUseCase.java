package com.manager.truck.truck.usecase.driver;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.TruckDriverAssignment;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.UserRole;
import com.manager.truck.truck.domain.UserRoleId;
import com.manager.truck.truck.domain.dto.request.DriverRequest;
import com.manager.truck.truck.domain.dto.response.DriverResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.repository.RoleRepository;
import com.manager.truck.truck.repository.UserRoleRepository;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.DriverService;
import com.manager.truck.truck.service.LicenseValidationService;
import com.manager.truck.truck.service.PersonService;
import com.manager.truck.truck.service.TruckDriverAssignmentService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class SaveDriverUseCase {

    private final DriverService driverService;
    private final PersonService personService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final LicenseValidationService licenseValidationService;
    private final TruckDriverAssignmentService assignmentService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public DriverResponse execute(DriverRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        licenseValidationService.validate(request.getLicenseCategory(), request.getLicenseExpiration());
        if (driverService.existsByDriverLicense(request.getDriverLicense().trim())) {
            throw new ConflictException("Ya existe un conductor con la licencia " + request.getDriverLicense());
        }

        Person person;
        if (request.getPersonId() != null) {
            person = personService.findById(request.getPersonId());
        } else {
            if (request.getEmail() != null && personService.existsByEmail(request.getEmail().trim())) {
                throw new ConflictException("Ya existe una persona registrada con el email " + request.getEmail());
            }
            person = new Person();
            person.setFirstName(request.getFirstName());
            person.setLastName(request.getLastName());
            person.setMaternalLastName(request.getMaternalLastName());
            person.setDocumentNumber(request.getDocumentNumber());
            person.setPhone(request.getPhone());
            person.setEmail(request.getEmail() != null ? request.getEmail().trim() : null);
            person.setState("ACTIVE");
            person = personService.save(person);
        }

        if (driverService.findOptionalById(person.getPersonId()).isPresent()) {
            throw new ConflictException("Esta persona ya está registrada como conductor");
        }

        // Si se provee contraseña y la persona no tiene usuario, creamos usuario con rol DRIVER
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (userService.findByEmail(person.getEmail()).isEmpty()) {
                User user = new User();
                user.setPersonId(person.getPersonId());
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                user.setState("ACTIVE");
                user = userService.save(user);

                Role driverRole = roleRepository.findByRoleName(SecurityRoles.DRIVER).orElse(null);
                if (driverRole != null) {
                    UserRole userRole = new UserRole();
                    userRole.setId(new UserRoleId(user.getUserId(), driverRole.getRoleId()));
                    userRole.setState("ACTIVE");
                    userRoleRepository.save(userRole);
                }
            }
        }

        Driver driver = new Driver();
        driver.setDriverId(person.getPersonId());
        driver.setDriverLicense(request.getDriverLicense().trim());
        driver.setLicenseCategory(licenseValidationService.normalizeCategory(request.getLicenseCategory()));
        driver.setLicenseExpiration(request.getLicenseExpiration());
        driver.setState("ACTIVE");
        Driver saved = driverService.save(driver);
        saved.setPerson(person);

        if (request.getTruckId() != null) {
            TruckDriverAssignment assignment = new TruckDriverAssignment();
            assignment.setTruckId(request.getTruckId());
            assignment.setDriverId(saved.getDriverId());
            assignment.setAssignedFrom(OffsetDateTime.now());
            assignment.setState("ACTIVE");
            assignmentService.save(assignment);
        }

        return DriverResponse.from(saved);
    }
}
