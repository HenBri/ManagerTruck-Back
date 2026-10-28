package com.manager.truck.truck.usecase.partner;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.Person;
import com.manager.truck.truck.domain.dto.request.PartnerUpdateRequest;
import com.manager.truck.truck.domain.dto.response.PartnerResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.PartnerService;
import com.manager.truck.truck.service.PersonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdatePartnerUseCase {

    private final PartnerService partnerService;
    private final PersonService personService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public PartnerResponse execute(Long partnerId, PartnerUpdateRequest request) {
        currentUserProvider.requireAdminOrSecretary();
        Partner partner = partnerService.findById(partnerId);
        if (request.getPartnerType() != null) {
            partner.setPartnerType(request.getPartnerType());
        }

        Person person = personService.findById(partner.getPartnerId());
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

        Partner saved = partnerService.update(partner);
        saved.setPerson(person);
        return PartnerResponse.from(saved);
    }
}
