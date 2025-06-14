package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Roles;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleResponse extends CommonResponse{

    private Roles role;

    public RoleResponse(Roles role) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.role = role;
    }
}
