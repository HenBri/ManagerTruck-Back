package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Charge;
import com.manager.truck.truck.domain.enums.ChargeState;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ChargeResponse extends CommonResponse {

    private Long chargeId;
    private Long companyId;
    private Long contractId;
    private String chargeDescription;
    private LocalDateTime chargeDate;
    private String chargeType;
    private ChargeState chargeState;

    public ChargeResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ChargeResponse from(Charge charge) {
        ChargeResponse response = new ChargeResponse();
        response.setChargeId(charge.getChargeId());
        response.setCompanyId(charge.getCompanyId());
        response.setContractId(charge.getContractId());
        response.setChargeDescription(charge.getChargeDescription());
        response.setChargeDate(charge.getChargeDate());
        response.setChargeType(charge.getChargeType());
        response.setChargeState(charge.getChargeState());
        return response;
    }
}
