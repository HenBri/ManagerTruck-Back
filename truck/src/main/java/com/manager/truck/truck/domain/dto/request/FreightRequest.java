package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class FreightRequest {

    @NotNull
    private Long contractId;

    @NotNull
    private Long containerId;

    private Long truckId;

    private Long auxiliaryProviderId;

    @Size(max = 20)
    private String auxiliaryTruckPlate;

    @Size(max = 255)
    private String freightDescription;

    @NotBlank
    @Size(max = 40)
    private String freightCode;

    private OffsetDateTime scheduledDeparture;

    private OffsetDateTime scheduledArrival;

    @DecimalMin(value = "0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal freightAmount;
}

