package com.manager.truck.truck.usecase.Company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateCompany {
    @Autowired
    private CompanyService companyService;

    public CompanyResponse execute(Long id, CompanyRequest companyRequest){
        Company company = companyBuild(id, companyRequest);
        companyService.update(company);

        return companyResponseBuild(company);

    }
    private Company companyBuild(Long id, CompanyRequest companyRequest){
        Company company = companyService.findById(id);
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
