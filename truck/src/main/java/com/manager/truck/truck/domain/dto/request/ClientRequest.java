package com.manager.truck.truck.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {
    private String name;
    private String phone;
    private String email;
    private String address;
}
