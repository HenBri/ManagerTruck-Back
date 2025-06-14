package com.manager.truck.truck.usecase.roles;

import com.manager.truck.truck.domain.Roles;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GetRoleByIdUseCase {
    @Autowired
    private RoleService roleService;

    public RoleResponse execute(Long id){
        Roles roles = roleService.findById(id);
        return new RoleResponse(roles);
    }
}
