package com.manager.truck.truck.domain.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PlatformUserRoleResponse extends CommonResponse {

    private Long userId;
    private String email;
    private String roleName;
    private LocalDateTime grantedAt;

    public PlatformUserRoleResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static PlatformUserRoleResponse of(Long userId, String email, String roleName, LocalDateTime grantedAt) {
        PlatformUserRoleResponse response = new PlatformUserRoleResponse();
        response.setUserId(userId);
        response.setEmail(email);
        response.setRoleName(roleName);
        response.setGrantedAt(grantedAt);
        return response;
    }
}
