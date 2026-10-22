package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.PartnerType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerRequest {

    @NotNull
    private PartnerType partnerType;

    @Size(max = 150)
    private String partnerName;

    @Size(max = 30)
    private String partnerDocument;

    @Size(max = 25)
    private String partnerPhone;

    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 8, max = 72)
    private String password;

    @Size(max = 100)
    private String name;

    @Size(max = 150)
    private String lastname;

    @Size(max = 30)
    private String ci;

    @Size(max = 25)
    private String phone;
}
