package com.manager.truck.truck.domain.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {
    private String name;
    private String address;
    private Integer phone;
    private String email;
}
