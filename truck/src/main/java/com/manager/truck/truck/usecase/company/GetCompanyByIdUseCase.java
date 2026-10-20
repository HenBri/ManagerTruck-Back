package com.manager.truck.truck.usecase.company;

import com.manager.truck.truck.domain.dto.response.CompanyResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetCompanyByIdUseCase {

    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public CompanyResponse execute(Long companyId) {
        if (!currentUserProvider.isSuperAdmin() && !currentUserProvider.isActiveMemberOf(companyId)) {
            throw new ForbiddenOperationException("No pertenece a esta empresa");
        }
        return CompanyResponse.from(companyService.findById(companyId));
    }
}
