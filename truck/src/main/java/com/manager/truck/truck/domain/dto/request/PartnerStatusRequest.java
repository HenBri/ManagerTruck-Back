package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerStatusRequest {

    @NotBlank
    private String state;
}
