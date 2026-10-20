package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientStatusRequest {

    @NotNull
    private Boolean clientState;
}
