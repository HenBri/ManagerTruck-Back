package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.constant.ResponseConstant;
import com.manager.truck.truck.domain.Charge;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChargesResponse extends CommonResponse{
    private List<Charge> charges;

    public ChargesResponse(List<Charge> charges) {
        super(ResponseConstant.StatusCodeResponse.SUCCESS_CODE, ResponseConstant.StatusCodeResponse.SUCCESS_MSG);
        this.charges = charges;
    }
}
