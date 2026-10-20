package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyMembershipsResponse extends CommonResponse {

    private List<CompanyMembershipResponse> memberships;

    public MyMembershipsResponse(List<CompanyMembershipResponse> memberships) {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
        this.memberships = memberships;
    }
}
