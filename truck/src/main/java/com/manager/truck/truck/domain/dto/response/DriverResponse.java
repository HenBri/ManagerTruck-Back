package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Driver;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
public class DriverResponse extends CommonResponse {

    private Long driverId;
    private String driverLicense;
    private String licenseCategory;
    private LocalDate licenseExpiration;
    private String firstName;
    private String lastName;
    private String maternalLastName;
    private String documentNumber;
    private String phone;
    private String email;
    private String state;
    private OffsetDateTime dateCreate;

    public DriverResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static DriverResponse from(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setDriverId(driver.getDriverId());
        response.setDriverLicense(driver.getDriverLicense());
        response.setLicenseCategory(driver.getLicenseCategory());
        response.setLicenseExpiration(driver.getLicenseExpiration());
        response.setState(driver.getState());
        response.setDateCreate(driver.getDateCreate());
        if (driver.getPerson() != null) {
            response.setFirstName(driver.getPerson().getFirstName());
            response.setLastName(driver.getPerson().getLastName());
            response.setMaternalLastName(driver.getPerson().getMaternalLastName());
            response.setDocumentNumber(driver.getPerson().getDocumentNumber());
            response.setPhone(driver.getPerson().getPhone());
            response.setEmail(driver.getPerson().getEmail());
        }
        return response;
    }

    public String getDriverName() {
        return firstName != null ? firstName + (lastName != null ? " " + lastName : "") : null;
    }

    public String getDriverCi() {
        return documentNumber;
    }

    public String getDriverPhone() {
        return phone;
    }

    public String getDriverState() {
        return state;
    }
}
