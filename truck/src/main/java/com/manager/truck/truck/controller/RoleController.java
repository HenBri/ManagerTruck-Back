package com.manager.truck.truck.controller;

import com.manager.truck.truck.domain.dto.request.RoleRequest;
import com.manager.truck.truck.domain.dto.response.RoleResponse;
import com.manager.truck.truck.domain.dto.response.RolesResponse;
import com.manager.truck.truck.usecase.roles.GetAllRoleUseCase;
import com.manager.truck.truck.usecase.roles.GetRoleByIdUseCase;
import com.manager.truck.truck.usecase.roles.SaveRoleUseCase;
import com.manager.truck.truck.usecase.roles.UpdateRoleUseCase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class RoleController {

    @Autowired
    private GetAllRoleUseCase getAllRoleUseCase;
    @Autowired
    private SaveRoleUseCase saveRoleUseCase;
    @Autowired
    private UpdateRoleUseCase updateRoleUseCase;
    @Autowired
    private GetRoleByIdUseCase getRoleByIdUseCase;


    @PostMapping("saveRole")
    public RoleResponse save(@RequestBody RoleRequest roleRequest){
        return saveRoleUseCase.execute(roleRequest);
    }
    @GetMapping("getAllRole")
    public RolesResponse getAll(){
        return getAllRoleUseCase.execute();
    }
    @PutMapping("/updateRole/{id}")
    public RoleResponse update(@PathVariable Long id,@RequestBody RoleRequest roleRequest){
        return updateRoleUseCase.execute(id, roleRequest);
    }
    @GetMapping("GetRoleById/{idRole}")
    public RoleResponse getRoleById(@PathVariable Long idRole){
        return getRoleByIdUseCase.execute(idRole);
    }
}
