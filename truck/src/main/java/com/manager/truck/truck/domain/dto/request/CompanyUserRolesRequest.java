package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CompanyUserRolesRequest {

    @NotEmpty
    private List<@NotBlank String> roleNames;
}
