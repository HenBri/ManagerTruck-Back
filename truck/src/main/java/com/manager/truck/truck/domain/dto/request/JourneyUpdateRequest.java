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
public class JourneyUpdateRequest {

    @Size(max = 255)
    private String journeyOrigin;

    @Size(max = 255)
    private String journeyDestination;

    private OffsetDateTime journeyStartDate;

    private OffsetDateTime journeyEndDate;

    @Size(max = 255)
    private String journeyDescription;

    @Size(max = 50)
    private String journeyType;

    private OffsetDateTime plannedStartDate;

    private OffsetDateTime plannedEndDate;

    private OffsetDateTime deliveryConfirmedAt;

    @Size(max = 150)
    private String recipientName;

    @Size(max = 255)
    private String proofOfDeliveryReference;

    private String observations;

    private Long assignmentId;

    @Size(max = 150)
    private String auxiliaryDriverName;

    @Size(max = 40)
    private String auxiliaryDriverLicense;

    @Size(max = 16)
    private String auxiliaryReason;

    @DecimalMin(value = "0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal agreedAmount;
}
