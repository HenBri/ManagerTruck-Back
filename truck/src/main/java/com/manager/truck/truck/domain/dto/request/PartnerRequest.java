package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.PartnerType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerRequest {

    @NotNull
    private PartnerType partnerType;

    @NotBlank
    @Size(max = 150)
    private String partnerName;

    @Size(max = 30)
    private String partnerDocument;

    @Size(max = 25)
    private String partnerPhone;

    private Long userId;
}
