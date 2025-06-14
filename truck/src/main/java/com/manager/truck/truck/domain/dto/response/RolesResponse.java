package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Roles;

import java.util.List;

public class RolesResponse extends CommonResponse{
    List<Roles> roles;

    public RolesResponse(List<Roles> roles) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.roles = roles;
    }
}
