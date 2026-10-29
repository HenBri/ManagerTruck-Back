package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CompanyResponse extends CommonResponse {
    private Long companyId;
    private String companyName;
    private String companyNit;
    private String companyAddress;
    private String companyPhone;
    private CompanyStatus companyStatus;
    private LocalDateTime companyCreatedAt;

    public CompanyResponse() { super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG); }

    public static CompanyResponse from(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.companyId = company.getCompanyId();
        response.companyName = company.getCompanyName();
        response.companyNit = company.getCompanyNit();
        response.companyAddress = company.getCompanyAddress();
        response.companyPhone = company.getCompanyPhone();
        response.companyStatus = company.getCompanyStatus();
        response.companyCreatedAt = company.getCompanyCreatedAt();
        return response;
    }
}
