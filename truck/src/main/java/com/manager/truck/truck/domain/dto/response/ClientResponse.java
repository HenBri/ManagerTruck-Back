package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Client;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientResponse  extends CommonResponse{

    private Client client;
    public ClientResponse(Client client) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.client=client;
    }
}