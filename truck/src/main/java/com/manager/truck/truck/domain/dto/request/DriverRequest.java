package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DriverRequest {

    @Size(max = 150)
    private String driverName;

    @Size(max = 30)
    private String driverCi;

    @Size(max = 25)
    private String driverPhone;

    @NotBlank
    @Size(max = 40)
    private String driverLicense;

    @Size(max = 20)
    private String licenseCategory;

    private LocalDate licenseExpiration;

    private Long personPartnerId;

    private Long truckId;

    @NotBlank
    @Email
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 8, max = 72)
    private String password;

    @NotBlank
    @Size(max = 100)
    private String name;

    @NotBlank
    @Size(max = 150)
    private String lastname;

    @Size(max = 30)
    private String ci;

    @Size(max = 25)
    private String phone;
}
