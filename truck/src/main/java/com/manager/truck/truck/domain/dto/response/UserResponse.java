package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UserResponse extends CommonResponse{
    private User user;
    public UserResponse(User user){
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.user = user;
    }
}
