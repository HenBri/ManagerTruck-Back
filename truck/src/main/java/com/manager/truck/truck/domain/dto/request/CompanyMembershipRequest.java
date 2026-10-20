package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyMembershipRequest {

    @NotBlank
    @Email
    private String userEmail;
}
