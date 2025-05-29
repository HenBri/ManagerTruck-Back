package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.Role;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String name;
    private String lastname;
    private String email;
    private Integer phone;
    private String ci;

    private Role role;
}
