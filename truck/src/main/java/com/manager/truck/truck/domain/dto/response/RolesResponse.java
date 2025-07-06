package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Roles;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter

public class RolesResponse extends CommonResponse{
    private List<Roles> roles;
    public RolesResponse(List<Roles> roles) {
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.roles = roles;
    }
}
