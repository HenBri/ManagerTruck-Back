package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.AuxiliaryProviderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuxiliaryProviderStatusRequest {

    @NotNull
    private AuxiliaryProviderStatus status;
}
