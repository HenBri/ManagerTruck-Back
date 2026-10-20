package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractsResponse extends CommonResponse {

    private List<ContractResponse> contracts;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public ContractsResponse(List<ContractResponse> contracts, int page, int size,
                             long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.contracts = contracts;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
