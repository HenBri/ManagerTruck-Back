package com.manager.truck.truck.usecase.companyuser;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.CompanyUserId;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.dto.request.CompanyMembershipRequest;
import com.manager.truck.truck.domain.dto.response.CompanyMembershipResponse;
import com.manager.truck.truck.domain.enums.CompanyStatus;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import com.manager.truck.truck.domain.enums.UserStatus;
import com.manager.truck.truck.exception.ConflictException;
import com.manager.truck.truck.exception.ResourceNotFoundException;
import com.manager.truck.truck.security.CurrentUserProvider;
import com.manager.truck.truck.service.CompanyService;
import com.manager.truck.truck.service.CompanyUserService;
import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaveCompanyUserUseCase {

    private final CompanyUserService companyUserService;
    private final CompanyService companyService;
    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public CompanyMembershipResponse execute(Long companyId, CompanyMembershipRequest request) {
        currentUserProvider.requireCompanyAdmin(companyId);
        if (companyService.findById(companyId).getCompanyStatus() != CompanyStatus.ACTIVE) {
            throw new ConflictException("No se pueden crear membresías en una empresa inactiva");
        }
        User user = userService.findByEmail(request.getUserEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado con el correo: " + request.getUserEmail()));
        if (user.getUserStatus() != UserStatus.ACTIVE) {
            throw new ConflictException("No se puede crear una membresía sobre una cuenta inactiva");
        }
        if (companyUserService.existsByCompanyIdAndUserId(companyId, user.getUserId())) {
            throw new ConflictException("El usuario ya es miembro de esta empresa");
        }
        CompanyUser membership = new CompanyUser();
        membership.setId(new CompanyUserId(companyId, user.getUserId()));
        membership.setMembershipStatus(MembershipStatus.ACTIVE);
        CompanyUser saved = companyUserService.save(membership);
        return CompanyMembershipResponse.from(saved, user, List.of());
    }
}
