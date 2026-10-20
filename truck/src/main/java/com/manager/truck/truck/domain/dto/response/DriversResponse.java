package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DriversResponse extends CommonResponse {

    private List<DriverResponse> drivers;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public DriversResponse(List<DriverResponse> drivers, int page, int size,
                           long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.drivers = drivers;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
