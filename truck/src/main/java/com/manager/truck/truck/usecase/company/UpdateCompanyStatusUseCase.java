package com.manager.truck.truck.usecase.company;

import com.manager.truck.truck.domain.Company;
import com.manager.truck.truck.domain.dto.request.CompanyStatusRequest;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCompanyStatusUseCase {

    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public CompanyResponse execute(Long companyId, CompanyStatusRequest request) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede cambiar el estado de una empresa");
        }
        Company company = companyService.findById(companyId);
        company.setCompanyStatus(request.getStatus());
        return CompanyResponse.from(companyService.update(company));
    }
}
