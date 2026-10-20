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
    private String name;
    private String nit;
    private String address;
    private String phone;
    private CompanyStatus status;
    private LocalDateTime createdAt;

    public CompanyResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static CompanyResponse from(Company company) {
        CompanyResponse response = new CompanyResponse();
        response.setCompanyId(company.getCompanyId());
        response.setName(company.getCompanyName());
        response.setNit(company.getCompanyNit());
        response.setAddress(company.getCompanyAddress());
        response.setPhone(company.getCompanyPhone());
        response.setStatus(company.getCompanyStatus());
        response.setCreatedAt(company.getCompanyCreatedAt());
        return response;
    }
}
