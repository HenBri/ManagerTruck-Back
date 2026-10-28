package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.FreightState;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FreightStatusRequest {

    @NotNull
    private FreightState state;
}

