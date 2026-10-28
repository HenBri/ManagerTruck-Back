package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Charge;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class ChargeResponse extends CommonResponse {

    private Long chargeId;
    private Long contractId;
    private String chargeDescription;
    private OffsetDateTime chargeDate;
    private String chargeType;
    private String cargoReference;
    private String goodsDescription;
    private Boolean hazardousGoods;
    private String handlingInstructions;
    private String state;
    private OffsetDateTime dateCreate;

    public ChargeResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static ChargeResponse from(Charge charge) {
        ChargeResponse response = new ChargeResponse();
        response.setChargeId(charge.getChargeId());
        response.setContractId(charge.getContractId());
        response.setChargeDescription(charge.getChargeDescription());
        response.setChargeDate(charge.getChargeDate());
        response.setChargeType(charge.getChargeType());
        response.setCargoReference(charge.getCargoReference());
        response.setGoodsDescription(charge.getGoodsDescription());
        response.setHazardousGoods(charge.getHazardousGoods());
        response.setHandlingInstructions(charge.getHandlingInstructions());
        response.setState(charge.getState());
        response.setDateCreate(charge.getDateCreate());
        return response;
    }

    public String getChargeState() {
        return state;
    }
}
