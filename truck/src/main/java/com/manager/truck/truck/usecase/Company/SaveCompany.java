package com.manager.truck.truck.usecase.Company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveCompany {
    @Autowired
    private CompanyService companyService;

    public CompanyResponse execute(CompanyRequest companyRequest){
        Company company = companyBuild(companyRequest);
        companyService.save(company);
        return companyResponseBuild(company);
    }
    private Company companyBuild(CompanyRequest companyRequest){
        Company company = new Company();
        company.setName(companyRequest.getName());
        company.setAddress(companyRequest.getAddress());
        company.setPhone(companyRequest.getPhone());
        company.setEmail(companyRequest.getEmail());
        return company;
    }

    private CompanyResponse companyResponseBuild(Company company){
        return new CompanyResponse(company);
    }
}
