package com.manager.truck.truck.domain.dto.request;

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
public class ContainerRequest {

    @NotNull
    private Long chargeId;

    @NotBlank
    @Size(max = 30)
    private String containerCode;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal containerWeight;

    @Size(max = 255)
    private String containerDescription;

    @Size(max = 50)
    private String containerType;

    @Size(max = 10)
    private String isoSize;

    @Size(max = 25)
    private String containerFormat;

    @Size(max = 50)
    private String sealNumber;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal grossWeightKg;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal tareWeightKg;

    @DecimalMin(value = "0")
    @Digits(integer = 9, fraction = 3)
    private BigDecimal cargoWeightKg;

    @Size(max = 120)
    private String shippingLine;

    @Size(max = 80)
    private String customsReference;

    private BigDecimal temperatureC;
}
