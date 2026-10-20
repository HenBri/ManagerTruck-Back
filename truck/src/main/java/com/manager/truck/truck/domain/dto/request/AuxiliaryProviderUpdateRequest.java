package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuxiliaryProviderUpdateRequest {

    @NotNull
    private AuxiliaryProviderKind providerKind;

    @NotBlank
    @Size(max = 150)
    private String providerName;

    @Size(max = 30)
    private String providerDocument;

    @Size(max = 25)
    private String providerPhone;

    @Size(max = 255)
    private String providerEmail;
}
