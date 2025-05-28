package com.manager.truck.truck.usecase.Company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.service.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteCompany {
    @Autowired
    private CompanyService companyService;

    public void execute(Long id){
        Company company = companyService.findById(id);
        company.setState(false);

        companyService.update(company);
    }




}
