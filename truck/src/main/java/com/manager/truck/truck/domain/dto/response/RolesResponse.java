package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RolesResponse extends CommonResponse {

    private List<RoleResponse> roles;

    public RolesResponse(List<RoleResponse> roles) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.roles = roles;
    }
}
