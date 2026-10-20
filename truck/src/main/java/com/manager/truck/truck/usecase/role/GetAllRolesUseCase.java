package com.manager.truck.truck.usecase.role;

import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.domain.dto.response.RolesResponse;
import com.manager.truck.truck.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetAllRolesUseCase {

    private final RoleService roleService;

    public RolesResponse execute() {
        List<RoleResponse> roles = roleService.findAll().stream()
                .map(RoleResponse::from)
                .toList();
        return new RolesResponse(roles);
    }
}
