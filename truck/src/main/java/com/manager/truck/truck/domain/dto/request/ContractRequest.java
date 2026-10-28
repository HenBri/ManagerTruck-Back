package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.OperationType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ContractRequest {

    @NotNull
    private Long clientId;

    @NotBlank
    @Size(max = 40)
    private String contractCode;

    private OperationType operationType = OperationType.IMPORTACION;

    @Size(max = 255)
    private String originLocation;

    @Size(max = 255)
    private String destinationLocation;

    private LocalDate expectedStartDate;

    private LocalDate expectedDeliveryDate;

    @Size(max = 80)
    private String referenceDocument;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal declaredTotalWeightKg;

    private Integer expectedContainerCount;

    private String observations;

    @NotNull
    @DecimalMin(value = "0")
    @Digits(integer = 13, fraction = 2)
    private BigDecimal contractAmount;

    @NotNull
    private LocalDate contractDate;

    @Size(max = 255)
    private String contractDescription;

    @Size(max = 50)
    private String contractType;
}
