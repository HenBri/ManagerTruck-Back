package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientsResponse extends CommonResponse {

    private List<ClientResponse> clients;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public ClientsResponse(List<ClientResponse> clients, int page, int size,
                           long totalElements, int totalPages) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.clients = clients;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }
}
