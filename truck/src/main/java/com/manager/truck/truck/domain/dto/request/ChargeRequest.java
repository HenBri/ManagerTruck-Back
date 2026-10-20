package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChargeRequest {

    @NotNull
    private Long contractId;

    @Size(max = 255)
    private String chargeDescription;

    @Size(max = 100)
    private String chargeType;
}
