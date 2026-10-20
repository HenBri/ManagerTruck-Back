package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Partner;
import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerResponse extends CommonResponse {

    private Long partnerId;
    private Long companyId;
    private Long userId;
    private PartnerType partnerType;
    private String partnerName;
    private String partnerDocument;
    private String partnerPhone;
    private PartnerStatus partnerState;

    public PartnerResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static PartnerResponse from(Partner partner) {
        PartnerResponse response = new PartnerResponse();
        response.setPartnerId(partner.getPartnerId());
        response.setCompanyId(partner.getCompanyId());
        response.setUserId(partner.getUserId());
        response.setPartnerType(partner.getPartnerType());
        response.setPartnerName(partner.getPartnerName());
        response.setPartnerDocument(partner.getPartnerDocument());
        response.setPartnerPhone(partner.getPartnerPhone());
        response.setPartnerState(partner.getPartnerState());
        return response;
    }
}
