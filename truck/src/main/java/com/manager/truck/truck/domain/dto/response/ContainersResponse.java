package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContainersResponse extends CommonResponse {

    private List<ContainerResponse> containers;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public ContainersResponse(List<ContainerResponse> containers, int page, int size,
                              long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.containers = containers;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
