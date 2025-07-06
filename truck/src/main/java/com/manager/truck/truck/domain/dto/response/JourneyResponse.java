package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Journey;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JourneyResponse extends CommonResponse{
    private Journey journey;

    public JourneyResponse(Journey journey) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.journey = journey;
    }
}
