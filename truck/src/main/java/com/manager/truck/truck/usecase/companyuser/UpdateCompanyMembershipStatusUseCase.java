package com.manager.truck.truck.usecase.companyuser;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.CompanyMembershipStatusRequest;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UpdateCompanyMembershipStatusUseCase {

    private final CompanyUserService companyUserService;
    private final CompanyService companyService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public CompanyMembershipResponse execute(Long companyId, Long userId,
                                             CompanyMembershipStatusRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        companyService.findById(companyId);
        CompanyUser membership = companyUserService
                .findByCompanyIdAndUserId(companyId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Membresía no encontrada para el usuario " + userId + " en la empresa " + companyId));
        if (membership.getMembershipStatus() == request.getStatus()) {
            throw new ConflictException("La membresía ya se encuentra en el estado " + request.getStatus());
        }
        membership.setMembershipStatus(request.getStatus());
        CompanyUser saved = companyUserService.save(membership);
        User user = userService.findById(userId);
        return CompanyMembershipResponse.from(saved, user,
                userService.findCompanyRoleNames(userId, companyId));
    }
}
