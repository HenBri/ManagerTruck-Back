package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UsersResponse extends CommonResponse {
    List<User> users;

    public UsersResponse(List<User> users){
        super(StatusCodeResponse.SUCCESS_CODE,StatusCodeResponse.SUCCESS_MSG);
        this.users = users;
    }
}
