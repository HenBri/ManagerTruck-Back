package com.manager.truck.truck.usecase.Company;

import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetCompanyById {
    @Autowired
    private CompanyService companyService;

    public CompanyResponse execute(Long id){
        return new CompanyResponse(companyService.findById(id));
    }
}
