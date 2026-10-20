package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuxiliaryProvidersResponse extends CommonResponse {

    private List<AuxiliaryProviderResponse> auxiliaryProviders;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public AuxiliaryProvidersResponse(List<AuxiliaryProviderResponse> auxiliaryProviders, int page, int size,
                                      long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.auxiliaryProviders = auxiliaryProviders;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
