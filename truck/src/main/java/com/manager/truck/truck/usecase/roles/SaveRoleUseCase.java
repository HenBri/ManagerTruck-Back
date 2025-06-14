package com.manager.truck.truck.usecase.roles;

import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.Roles;
import com.manager.truck.truck.domain.dto.request.RoleRequest;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SaveRoleUseCase {
    @Autowired
    private RoleService roleService;

    public RoleResponse execute(RoleRequest roleRequest){
        Roles roles = roleBuild(roleRequest);
        roleService.save(roles);
        return roleResponseBuild(roles);
    }

    private Roles roleBuild(RoleRequest roleRequest){
        Roles roles = new Roles();
        roles.setRole(Role.valueOf(roleRequest.getRole().toUpperCase()));
        roles.setDescription(roleRequest.getDescription());
        return roles;
    }
    private RoleResponse roleResponseBuild(Roles roles){
        return new RoleResponse(roles);
    }
}
