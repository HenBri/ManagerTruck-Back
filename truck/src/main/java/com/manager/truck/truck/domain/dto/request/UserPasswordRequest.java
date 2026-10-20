package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPasswordRequest {

    @NotBlank
    private String currentPassword;

    @NotBlank
    @Size(min = 8, max = 72)
    private String newPassword;
}
