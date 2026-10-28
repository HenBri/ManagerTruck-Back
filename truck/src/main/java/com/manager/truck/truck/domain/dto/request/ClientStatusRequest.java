package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientStatusRequest {

    @NotBlank
    private String state;
}
