package com.manager.truck.truck.usecase.companyuser;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipsResponse;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCompanyMembershipsUseCase {

    private final CompanyUserService companyUserService;
    private final CompanyService companyService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public CompanyMembershipsResponse execute(Long companyId, Pageable pageable) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        Page<CompanyMembershipResponse> page = companyUserService.findByCompanyId(companyId, pageable)
                .map(membership -> toResponse(membership, companyId));
        return new CompanyMembershipsResponse(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }

    private CompanyMembershipResponse toResponse(CompanyUser membership, Long companyId) {
        User user = userService.findById(membership.getId().getUserId());
        List<String> roleNames = userService.findCompanyRoleNames(user.getUserId(), companyId);
        return CompanyMembershipResponse.from(membership, user, roleNames);
    }
}
