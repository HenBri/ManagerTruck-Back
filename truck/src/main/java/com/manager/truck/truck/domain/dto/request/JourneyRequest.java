package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.AuxiliaryReason;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class JourneyRequest {

    @NotNull
    private Long containerId;

    @NotBlank
    @Size(max = 255)
    private String journeyOrigin;

    @NotBlank
    @Size(max = 255)
    private String journeyDestination;

    @Size(max = 255)
    private String journeyDescription;

    @Size(max = 50)
    private String journeyType;

    @NotNull
    private JourneyExecutionType executionType;

    private Long assignmentId;

    private Long auxiliaryProviderId;

    @Size(max = 20)
    private String auxiliaryTruckPlate;

    @Size(max = 150)
    private String auxiliaryDriverName;

    @Size(max = 40)
    private String auxiliaryDriverLicense;

    private AuxiliaryReason auxiliaryReason;

    @DecimalMin(value = "0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal agreedAmount;
}
