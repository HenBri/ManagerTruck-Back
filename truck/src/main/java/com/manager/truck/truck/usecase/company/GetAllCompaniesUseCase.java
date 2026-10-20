package com.manager.truck.truck.usecase.company;

import com.manager.truck.truck.domain.dto.response.CompaniesResponse;
import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllCompaniesUseCase {

    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public CompaniesResponse execute(Pageable pageable) {
        if (!currentUserProvider.isSuperAdmin()) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN puede listar empresas");
        }
        Page<CompanyResponse> page = companyService.findAll(pageable).map(CompanyResponse::from);
        List<CompanyResponse> companies = page.getContent();
        return new CompaniesResponse(companies, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
