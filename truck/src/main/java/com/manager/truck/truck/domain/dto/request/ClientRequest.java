package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRequest {

    @NotBlank
    @Size(max = 150)
    private String clientName;

    @Size(max = 30)
    private String clientCi;

    @Email
    @Size(max = 255)
    private String clientEmail;

    @Size(max = 25)
    private String clientPhone;

    @Size(max = 255)
    private String clientAddress;

    @Size(max = 30)
    private String taxId;

    @Size(max = 150)
    private String contactPerson;

    @Size(max = 25)
    private String contactPhone;
}
