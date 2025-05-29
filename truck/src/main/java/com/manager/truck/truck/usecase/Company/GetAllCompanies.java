package com.manager.truck.truck.usecase.Company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.response.CompaniesResponse;
import com.manager.truck.truck.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllCompanies {
    @Autowired
    private CompanyService companyService;

    public CompaniesResponse execute(){
        List<Company> companies = companyService.findAll();
        return new CompaniesResponse(companies);
    }
}
