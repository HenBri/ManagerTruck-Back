package com.manager.truck.truck.domain.dto.request;

import com.manager.truck.truck.domain.enums.PartnerType;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PartnerUpdateRequest {

    private PartnerType partnerType;

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

    public String getPartnerName() {
        return firstName != null ? firstName + (lastName != null ? " " + lastName : "") : null;
    }

    public void setPartnerName(String name) {
        if (this.firstName == null && name != null) {
            String[] parts = name.split(" ", 2);
            this.firstName = parts[0];
            if (parts.length > 1) {
                this.lastName = parts[1];
            }
        }
    }

    public String getPartnerDocument() {
        return documentNumber;
    }

    public void setPartnerDocument(String doc) {
        if (this.documentNumber == null) this.documentNumber = doc;
    }

    public String getPartnerPhone() {
        return phone;
    }

    public void setPartnerPhone(String p) {
        if (this.phone == null) this.phone = p;
    }
}
