package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChargesResponse extends CommonResponse {

    private List<ChargeResponse> charges;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public ChargesResponse(List<ChargeResponse> charges, int page, int size,
                           long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.charges = charges;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
