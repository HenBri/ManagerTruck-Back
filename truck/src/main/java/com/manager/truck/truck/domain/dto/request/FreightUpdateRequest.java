package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
public class FreightUpdateRequest {

    private Long truckId;

    private Long auxiliaryProviderId;

    @Size(max = 20)
    private String auxiliaryTruckPlate;

    @Size(max = 255)
    private String freightDescription;

    private OffsetDateTime scheduledDeparture;

    private OffsetDateTime scheduledArrival;

    private OffsetDateTime actualDeparture;

    private OffsetDateTime actualArrival;

    private String deliveryNotes;

    @DecimalMin(value = "0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal freightAmount;
}

