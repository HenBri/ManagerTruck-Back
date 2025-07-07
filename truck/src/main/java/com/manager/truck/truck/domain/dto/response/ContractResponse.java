package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Contract;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContractResponse extends CommonResponse{
    private Contract contract;

    public ContractResponse(Contract contract) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.contract = contract;
    }
}
