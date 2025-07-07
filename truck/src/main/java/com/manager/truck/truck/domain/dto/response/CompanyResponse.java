package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.constant.ResponseConstant;
import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyResponse extends CommonResponse{
    private Company company;

    public CompanyResponse(Company company) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.company = company;
    }
}
