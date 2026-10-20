package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TrucksResponse extends CommonResponse {

    private List<TruckResponse> trucks;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public TrucksResponse(List<TruckResponse> trucks, int page, int size,
                          long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.trucks = trucks;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
