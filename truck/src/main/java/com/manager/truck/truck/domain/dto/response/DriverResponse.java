package com.manager.truck.truck.domain.dto.response;

import com.manager.truck.truck.domain.Driver;
import com.manager.truck.truck.domain.enums.DriverStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DriverResponse extends CommonResponse {

    private Long driverId;
    private Long companyId;
    private Long userId;
    private Long personPartnerId;
    private String driverName;
    private String driverCi;
    private String driverPhone;
    private String driverLicense;
    private String licenseCategory;
    private LocalDate licenseExpiration;
    private DriverStatus driverState;

    public DriverResponse() {
        super(StatusCodeResponse.SUCCESS_CODE, StatusCodeResponse.SUCCESS_MSG);
    }

    public static DriverResponse from(Driver driver) {
        DriverResponse response = new DriverResponse();
        response.setDriverId(driver.getDriverId());
        response.setCompanyId(driver.getCompanyId());
        response.setUserId(driver.getUserId());
        response.setPersonPartnerId(driver.getPersonPartnerId());
        response.setDriverName(driver.getDriverName());
        response.setDriverCi(driver.getDriverCi());
        response.setDriverPhone(driver.getDriverPhone());
        response.setDriverLicense(driver.getDriverLicense());
        response.setLicenseCategory(driver.getLicenseCategory());
        response.setLicenseExpiration(driver.getLicenseExpiration());
        response.setDriverState(driver.getDriverState());
        return response;
    }
}
