package com.manager.truck.truck.domain.dto.request;

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
public class TruckRequest {

    @NotNull
    private Long ownerPartnerId;

    @NotBlank
    @Size(max = 20)
    private String truckBoard;

    @Size(max = 50)
    private String truckBrand;

    @Size(max = 50)
    private String truckModel;

    @Size(max = 50)
    private String truckChassis;

    @Size(max = 30)
    private String truckColor;

    private Integer truckYear;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal truckCapacity;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal truckEmptyWeightKg;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal trailerEmptyWeightKg;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal maxGrossWeightKg;

    @Size(max = 50)
    private String truckType;

    @Size(max = 20)
    private String chassisPlate;

    private LocalDate insuranceExpiration;

    private LocalDate technicalInspectionExpiration;
}
