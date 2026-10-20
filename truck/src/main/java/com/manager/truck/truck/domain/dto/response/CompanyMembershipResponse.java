package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.CompanyUser;
import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.enums.MembershipStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CompanyMembershipResponse extends CommonResponse {

    private Long companyId;
    private Long userId;
    private String email;
    private String name;
    private String lastname;
    private MembershipStatus membershipStatus;
    private LocalDateTime joinedAt;
    private List<String> roleNames;

    public CompanyMembershipResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static CompanyMembershipResponse from(CompanyUser membership, User user, List<String> roleNames) {
        CompanyMembershipResponse response = new CompanyMembershipResponse();
        response.setCompanyId(membership.getId().getCompanyId());
        response.setUserId(membership.getId().getUserId());
        response.setEmail(user.getUserEmail());
        response.setName(user.getUserName());
        response.setLastname(user.getUserLastname());
        response.setMembershipStatus(membership.getMembershipStatus());
        response.setJoinedAt(membership.getJoinedAt());
        response.setRoleNames(roleNames);
        return response;
    }
}
