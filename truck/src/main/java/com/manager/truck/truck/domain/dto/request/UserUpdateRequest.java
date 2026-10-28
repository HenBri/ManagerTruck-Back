package com.manager.truck.truck.domain.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest {

    @NotBlank
    @Size(max = 100)
    private String firstName;

    @NotBlank
    @Size(max = 100)
    private String lastName;

    @Size(max = 100)
    private String maternalLastName;

    @Size(max = 30)
    private String documentNumber;

    @Size(max = 25)
    private String phone;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getMaternalLastName() {
        return maternalLastName;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public String getPhone() {
        return phone;
    }

    public String getName() {
        return firstName;
    }

    public void setName(String name) {
        if (this.firstName == null) {
            this.firstName = name;
        }
    }

    public String getLastname() {
        return lastName;
    }

    public void setLastname(String lastname) {
        if (this.lastName == null) {
            this.lastName = lastname;
        }
    }

    public String getCi() {
        return documentNumber;
    }

    public void setCi(String ci) {
        if (this.documentNumber == null) {
            this.documentNumber = ci;
        }
    }
}
