package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyMembershipsResponse extends CommonResponse {

    private List<CompanyMembershipResponse> memberships;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public CompanyMembershipsResponse(List<CompanyMembershipResponse> memberships, int page, int size,
                                      long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.memberships = memberships;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
