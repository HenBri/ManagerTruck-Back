package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.CompanyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyStatusRequest {

    @NotNull
    private CompanyStatus status;
}
