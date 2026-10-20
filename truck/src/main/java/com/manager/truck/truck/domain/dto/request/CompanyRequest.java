package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyRequest {

    @NotBlank
    @Size(max = 150)
    private String name;

    @Size(max = 30)
    private String nit;

    @Size(max = 255)
    private String address;

    @Size(max = 25)
    private String phone;
}
