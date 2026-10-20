package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyUserRolesResponse extends CommonResponse {

    private Long companyId;
    private Long userId;
    private List<String> roleNames;

    public CompanyUserRolesResponse(Long companyId, Long userId, List<String> roleNames) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.companyId = companyId;
        this.userId = userId;
        this.roleNames = roleNames;
    }
}
