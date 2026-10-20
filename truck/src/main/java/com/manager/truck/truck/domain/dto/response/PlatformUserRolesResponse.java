package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlatformUserRolesResponse extends CommonResponse {

    private List<PlatformUserRoleResponse> platformRoles;

    public PlatformUserRolesResponse(List<PlatformUserRoleResponse> platformRoles) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.platformRoles = platformRoles;
    }
}
