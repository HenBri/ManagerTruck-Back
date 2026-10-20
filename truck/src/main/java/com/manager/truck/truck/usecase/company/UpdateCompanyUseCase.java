package com.manager.truck.truck.usecase.company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.CompanyRequest;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCompanyUseCase {

    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public CompanyResponse execute(Long companyId, CompanyRequest request) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede modificar empresas");
        }
        Company company = companyService.findById(companyId);
        company.setCompanyName(request.getName());
        company.setCompanyNit(request.getNit());
        company.setCompanyAddress(request.getAddress());
        company.setCompanyPhone(request.getPhone());
        return CompanyResponse.from(companyService.update(company));
    }
}
