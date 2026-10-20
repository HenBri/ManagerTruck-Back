package com.manager.truck.truck.usecase.role;

import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetRoleByIdUseCase {

    private final RoleService roleService;

    public RoleResponse execute(Integer roleId) {
        return RoleResponse.from(roleService.findById(roleId));
    }
}
