package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.RoleScope;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {

    @NotBlank
    @Size(max = 50)
    private String name;

    @NotNull
    private RoleScope scope;

    @Size(max = 255)
    private String description;
}
