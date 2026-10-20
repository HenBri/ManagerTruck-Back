package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TruckDriverAssignmentsResponse extends CommonResponse {

    private List<TruckDriverAssignmentResponse> assignments;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public TruckDriverAssignmentsResponse(List<TruckDriverAssignmentResponse> assignments, int page, int size,
                                          long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.assignments = assignments;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
