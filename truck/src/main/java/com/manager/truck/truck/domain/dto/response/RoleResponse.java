package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class RoleResponse extends CommonResponse {

    private Integer roleId;
    private String name;
    private String description;
    private String state;
    private OffsetDateTime dateCreate;

    public RoleResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static RoleResponse from(Role role) {
        RoleResponse response = new RoleResponse();
        response.setRoleId(role.getRoleId());
        response.setName(role.getRoleName());
        response.setDescription(role.getRoleDescription());
        response.setState(role.getState());
        response.setDateCreate(role.getDateCreate());
        return response;
    }
}
