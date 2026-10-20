package com.manager.truck.truck.usecase.user;

import com.manager.truck.truck.domain.dto.response.UserResponse;
import com.manager.truck.truck.domain.dto.response.UsersResponse;
import com.manager.truck.truck.exception.ForbiddenOperationException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.security.SecurityRoles;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetCompanyUsersUseCase {

    private final UserService userService;
    private final CompanyService companyService;
    private final CurrentUserProvider currentUserProvider;

    public UsersResponse execute(Long companyId, Pageable pageable) {
        if (!currentUserProvider.isSuperAdmin()
                && !currentUserProvider.hasCompanyRole(companyId, SecurityRoles.SECRETARY)) {
            throw new ForbiddenOperationException("Solo SUPER_ADMIN o un SECRETARY de la empresa puede ver sus miembros");
        }
        companyService.findById(companyId);
        Page<UserResponse> page = userService.findCompanyMembers(companyId, pageable).map(UserResponse::from);
        List<UserResponse> users = page.getContent();
        return new UsersResponse(users, page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
