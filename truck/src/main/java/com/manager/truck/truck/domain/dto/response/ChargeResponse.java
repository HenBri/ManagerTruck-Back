package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Charge;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeResponse extends CommonResponse{
    private Charge charge;

    public ChargeResponse(Charge charge) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.charge = charge;
    }
}
