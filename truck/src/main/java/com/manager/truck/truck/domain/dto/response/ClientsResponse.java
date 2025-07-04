package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.constant.ResponseConstant;
import com.manager.truck.truck.domain.Client;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ClientsResponse extends CommonResponse{

    private List<Client> clients;


    public ClientsResponse(List<Client> clients) {

        super(ResponseConstant.StatusCodeResponse.SUCCESS_CODE, ResponseConstant.StatusCodeResponse.SUCCESS_MSG);
        this.clients=clients;
    }
}