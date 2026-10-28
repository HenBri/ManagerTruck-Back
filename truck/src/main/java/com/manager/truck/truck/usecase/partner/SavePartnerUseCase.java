package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.UserRole;
import com.manager.truck.truck.domain.UserRoleId;
import com.manager.truck.truck.domain.dto.request.PartnerRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.domain.enums.PartnerType;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.repository.RoleRepository;
import com.manager.truck.truck.repository.UserRoleRepository;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.PersonService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SavePartnerUseCase {

    private final PartnerService partnerService;
    private final PersonService personService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PartnerResponse execute(PartnerRequest request) {
        currentUserProvider.requireAdminOrSecretary();

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

        if (partnerService.findOptionalById(person.getPersonId()).isPresent()) {
            throw new ConflictException("Esta persona ya está registrada como socio");
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            if (userService.findByEmail(person.getEmail()).isEmpty()) {
                User user = new User();
                user.setPersonId(person.getPersonId());
                user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
                user.setState("ACTIVE");
                user = userService.save(user);

                Role partnerRole = roleRepository.findByRoleName(SecurityRoles.PARTNER).orElse(null);
                if (partnerRole != null) {
                    UserRole userRole = new UserRole();
                    userRole.setId(new UserRoleId(user.getUserId(), partnerRole.getRoleId()));
                    userRole.setState("ACTIVE");
                    userRoleRepository.save(userRole);
                }
            }
        }

        Partner partner = new Partner();
        partner.setPartnerId(person.getPersonId());
        partner.setPartnerType(request.getPartnerType() != null ? request.getPartnerType() : PartnerType.PERSON);
        partner.setState("ACTIVE");
        Partner saved = partnerService.save(partner);
        saved.setPerson(person);
        return PartnerResponse.from(saved);
    }
}
