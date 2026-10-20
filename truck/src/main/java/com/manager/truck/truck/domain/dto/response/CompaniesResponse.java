package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompaniesResponse extends CommonResponse {

    private List<CompanyResponse> companies;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public CompaniesResponse(List<CompanyResponse> companies, int page, int size, long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.companies = companies;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
