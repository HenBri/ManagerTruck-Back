package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.response.CompaniesResponse;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.usecase.Company.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("api/company")
public class CompanyController {
    @Autowired
    private SaveCompany saveCompany;
    @Autowired
    private UpdateCompany updateCompany;
    @Autowired
    private GetAllCompanies getAllCompanies;
    @Autowired
    private GetCompanyById getCompanyById;
    @Autowired
    private DeleteCompany deleteCompany;


    @PostMapping("save")
    public CompanyResponse save(@RequestBody CompanyRequest companyRequest){
        return saveCompany.execute(companyRequest);
    }

    @GetMapping("company/{id}")
    public CompanyResponse getById(@PathVariable Long id){
        return getCompanyById.execute(id);
    }

    @GetMapping("getAll")
    public CompaniesResponse getAllCompanies(){
        return getAllCompanies.execute();
    }
    @PutMapping("post/{id}")
    public CompanyResponse update(@PathVariable Long id,CompanyRequest companyRequest){
        return updateCompany.execute(id, companyRequest);
    }
    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id){
        deleteCompany.execute(id);
    }



}
