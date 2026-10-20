package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.PartnerStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerStatusRequest {

    @NotNull
    private PartnerStatus status;
}
