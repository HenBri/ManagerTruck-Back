package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DriverUpdateRequest {

    @NotBlank
    @Size(max = 40)
    private String driverLicense;

    @NotBlank
    @Size(max = 20)
    private String licenseCategory;

    private LocalDate licenseExpiration;

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Size(max = 100)
    private String maternalLastName;

    @Size(max = 30)
    private String documentNumber;

    @Size(max = 25)
    private String phone;

    @Size(max = 255)
    private String email;

    public String getDriverName() {
        return firstName != null ? firstName + (lastName != null ? " " + lastName : "") : null;
    }

    public void setDriverName(String name) {
        if (this.firstName == null && name != null) {
            String[] parts = name.split(" ", 2);
            this.firstName = parts[0];
            if (parts.length > 1) this.lastName = parts[1];
        }
    }

    public String getDriverCi() {
        return documentNumber;
    }

    public void setDriverCi(String ci) {
        if (this.documentNumber == null) this.documentNumber = ci;
    }

    public String getDriverPhone() {
        return phone;
    }

    public void setDriverPhone(String p) {
        if (this.phone == null) this.phone = p;
    }
}
