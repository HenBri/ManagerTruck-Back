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

    private Long personId;

    @NotNull
    private PartnerType partnerType = PartnerType.PERSON;

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

    @Email
    @Size(max = 255)
    private String email;

    @Size(min = 8, max = 72)
    private String password;

    // Compatibilidad hacia atrás
    public String getPartnerName() {
        return firstName != null ? firstName + (lastName != null ? " " + lastName : "") : null;
    }

    public void setPartnerName(String name) {
        if (this.firstName == null && name != null) {
            String[] parts = name.split(" ", 2);
            this.firstName = parts[0];
            if (parts.length > 1) {
                this.lastName = parts[1];
            } else {
                this.lastName = "";
            }
        }
    }

    public String getPartnerDocument() {
        return documentNumber;
    }

    public void setPartnerDocument(String doc) {
        if (this.documentNumber == null) {
            this.documentNumber = doc;
        }
    }

    public String getPartnerPhone() {
        return phone;
    }

    public void setPartnerPhone(String phone) {
        if (this.phone == null) {
            this.phone = phone;
        }
    }

    public void setName(String name) {
        if (this.firstName == null) this.firstName = name;
    }

    public void setLastname(String lastname) {
        if (this.lastName == null) this.lastName = lastname;
    }

    public void setCi(String ci) {
        if (this.documentNumber == null) this.documentNumber = ci;
    }
}
