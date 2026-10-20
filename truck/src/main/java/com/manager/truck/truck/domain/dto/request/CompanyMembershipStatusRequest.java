package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.MembershipStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyMembershipStatusRequest {

    @NotNull
    private MembershipStatus status;
}
