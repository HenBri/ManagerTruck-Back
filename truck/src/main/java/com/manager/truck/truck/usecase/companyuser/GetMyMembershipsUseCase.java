package com.manager.truck.truck.usecase.companyuser;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.dto.response.MyMembershipsResponse;
import com.manager.truck.truck.security.CurrentUser;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetMyMembershipsUseCase {

    private final CompanyUserService companyUserService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public MyMembershipsResponse execute() {
        CurrentUser currentUser = currentUserProvider.get();
        User user = userService.findById(currentUser.userId());
        List<CompanyMembershipResponse> memberships = companyUserService
                .findByUserId(currentUser.userId())
                .stream()
                .map(membership -> toResponse(membership, user))
                .toList();
        return new MyMembershipsResponse(memberships);
    }

    private CompanyMembershipResponse toResponse(CompanyUser membership, User user) {
        Long companyId = membership.getId().getCompanyId();
        List<String> roleNames = userService.findCompanyRoleNames(user.getUserId(), companyId);
        return CompanyMembershipResponse.from(membership, user, roleNames);
    }
}
