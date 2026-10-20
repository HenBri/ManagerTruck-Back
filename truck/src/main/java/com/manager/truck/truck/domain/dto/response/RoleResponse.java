package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Role;
import com.manager.truck.truck.domain.enums.RoleScope;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse extends CommonResponse {

    private Integer roleId;
    private String name;
    private RoleScope scope;
    private String description;

    public RoleResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static RoleResponse from(Role role) {
        RoleResponse response = new RoleResponse();
        response.setRoleId(role.getRoleId());
        response.setName(role.getRoleName());
        response.setScope(role.getRoleScope());
        response.setDescription(role.getRoleDescription());
        return response;
    }
}
