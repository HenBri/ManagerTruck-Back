package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Company;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompaniesResponse extends CommonResponse{
    List<Company> companies;

    public CompaniesResponse(List<Company> companies){
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.companies = companies;
    }
}
