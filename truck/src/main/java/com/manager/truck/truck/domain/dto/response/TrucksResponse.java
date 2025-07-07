package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.constant.ResponseConstant;
import com.manager.truck.truck.domain.Constants;
import com.manager.truck.truck.domain.Truck;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrucksResponse extends CommonResponse{

    List<Truck> trucks;

    public TrucksResponse(List<Truck> trucks) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.trucks = trucks;
    }
}
