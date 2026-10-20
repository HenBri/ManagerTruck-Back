package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.domain.enums.UserStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserResponse extends CommonResponse {

    private Long userId;
    private String email;
    private String name;
    private String lastname;
    private String ci;
    private String phone;
    private UserStatus status;
    private LocalDateTime createdAt;

    public UserResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static UserResponse from(User user) {
        UserResponse response = new UserResponse();
        response.setUserId(user.getUserId());
        response.setEmail(user.getUserEmail());
        response.setName(user.getUserName());
        response.setLastname(user.getUserLastname());
        response.setCi(user.getUserCi());
        response.setPhone(user.getUserPhone());
        response.setStatus(user.getUserStatus());
        response.setCreatedAt(user.getUserCreatedAt());
        return response;
    }
}
