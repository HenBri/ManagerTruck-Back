package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.DriverStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = Constants.DriverTable.NAME)
public class Driver {

    @Id
    @Column(name = Constants.DriverTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driverId;

    @Column(name = Constants.DriverTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.DriverTable.UserId.NAME)
    private Long userId;

    @Column(name = Constants.DriverTable.PersonPartnerId.NAME)
    private Long personPartnerId;

    @Column(name = Constants.DriverTable.Name.NAME, length = Constants.DriverTable.Name.LENGTH, nullable = false)
    private String driverName;

    @Column(name = Constants.DriverTable.Ci.NAME, length = Constants.DriverTable.Ci.LENGTH)
    private String driverCi;

    @Column(name = Constants.DriverTable.Phone.NAME, length = Constants.DriverTable.Phone.LENGTH)
    private String driverPhone;

    @Column(name = Constants.DriverTable.License.NAME, length = Constants.DriverTable.License.LENGTH, nullable = false)
    private String driverLicense;

    @Column(name = Constants.DriverTable.LicenseCategory.NAME, length = Constants.DriverTable.LicenseCategory.LENGTH)
    private String licenseCategory;

    @Column(name = Constants.DriverTable.LicenseExpiration.NAME)
    private LocalDate licenseExpiration;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.DriverTable.State.NAME, nullable = false)
    private DriverStatus driverState;
}
