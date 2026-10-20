package com.manager.truck.truck.usecase.company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaveCompanyUseCase {

    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public CompanyResponse execute(CompanyRequest request) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede crear empresas");
        }
        Company company = new Company();
        company.setCompanyName(request.getName());
        company.setCompanyNit(request.getNit());
        company.setCompanyAddress(request.getAddress());
        company.setCompanyPhone(request.getPhone());
        company.setCompanyStatus(CompanyStatus.ACTIVE);
        Company saved = companyService.save(company);
        return CompanyResponse.from(companyService.findById(saved.getCompanyId()));
    }
}
