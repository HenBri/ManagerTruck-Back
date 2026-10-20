package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.TruckStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TruckStatusRequest {

    @NotNull
    private TruckStatus status;
}
