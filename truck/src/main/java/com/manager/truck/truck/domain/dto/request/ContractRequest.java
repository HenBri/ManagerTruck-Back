package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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
