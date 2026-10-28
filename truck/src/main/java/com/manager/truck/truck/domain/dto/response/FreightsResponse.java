package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FreightsResponse extends CommonResponse {

    private List<FreightResponse> freights;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public FreightsResponse(List<FreightResponse> freights, int page, int size,
                            long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.freights = freights;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}

