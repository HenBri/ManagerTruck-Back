package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 150)
    private String lastname;

    @Size(max = 30)
    private String ci;

    @Size(max = 25)
    private String phone;
}
