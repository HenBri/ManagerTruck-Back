package com.manager.truck.truck.usecase.roles;

import com.manager.truck.truck.domain.Roles;
import com.manager.truck.truck.domain.dto.response.RolesResponse;
import com.manager.truck.truck.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllRoleUseCase {
    @Autowired
    private RoleService roleService;

    public RolesResponse execute(){
        List<Roles> roles=roleService.findAll();
         return new RolesResponse(roles);
    }

}
