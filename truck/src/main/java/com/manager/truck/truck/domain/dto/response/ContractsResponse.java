package com.manager.truck.truck.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.manager.truck.truck.domain.Contract;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ContractsResponse extends CommonResponse{
    private List<Contract> contracts;

    public ContractsResponse(List<Contract> contracts) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.contracts = contracts;
    }
}
