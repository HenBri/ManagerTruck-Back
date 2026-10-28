package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.enums.PartnerType;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class PartnerResponse extends CommonResponse {

    private Long partnerId;
    private PartnerType partnerType;
    private String firstName;
    private String lastName;
    private String maternalLastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String state;
    private OffsetDateTime dateCreate;

    public PartnerResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static PartnerResponse from(Partner partner) {
        PartnerResponse response = new PartnerResponse();
        response.setPartnerId(partner.getPartnerId());
        response.setPartnerType(partner.getPartnerType());
        response.setState(partner.getState());
        response.setDateCreate(partner.getDateCreate());
        if (partner.getPerson() != null) {
            response.setFirstName(partner.getPerson().getFirstName());
            response.setLastName(partner.getPerson().getLastName());
            response.setMaternalLastName(partner.getPerson().getMaternalLastName());
            response.setDocumentNumber(partner.getPerson().getDocumentNumber());
            response.setPhone(partner.getPerson().getPhone());
            response.setEmail(partner.getPerson().getEmail());
        }
        return response;
    }

    public String getPartnerName() {
        return firstName != null ? firstName + (lastName != null ? " " + lastName : "") : null;
    }

    public String getPartnerDocument() {
        return documentNumber;
    }

    public String getPartnerPhone() {
        return phone;
    }

    public String getPartnerState() {
        return state;
    }
}
