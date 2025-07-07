package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.constant.ResponseConstant;
import com.manager.truck.truck.domain.Truck;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TruckResponse extends CommonResponse{

    private Truck newTruck;

    public TruckResponse(Truck truck) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.newTruck = truck;
    }
}
