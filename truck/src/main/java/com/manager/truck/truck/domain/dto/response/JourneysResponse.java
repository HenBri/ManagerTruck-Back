package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Journey;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JourneysResponse extends CommonResponse{
    List<Journey> journeys;

    public JourneysResponse(List<Journey> journeys) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.journeys  =journeys;
    }
}
