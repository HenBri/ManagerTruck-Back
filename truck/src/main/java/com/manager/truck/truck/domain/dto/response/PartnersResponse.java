package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PartnersResponse extends CommonResponse {

    private List<PartnerResponse> partners;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public PartnersResponse(List<PartnerResponse> partners, int page, int size,
                            long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.partners = partners;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
