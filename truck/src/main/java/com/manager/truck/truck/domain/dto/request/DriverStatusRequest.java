package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.DriverStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DriverStatusRequest {

    @NotNull
    private DriverStatus status;
}
