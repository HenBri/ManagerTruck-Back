package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class JourneysResponse extends CommonResponse {

    private List<JourneyResponse> journeys;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public JourneysResponse(List<JourneyResponse> journeys, int page, int size,
                            long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.journeys = journeys;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
