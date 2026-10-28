package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.DriverTable.NAME)
public class Driver {

    @Id
    @Column(name = Constants.DriverTable.Id.NAME, nullable = false)
    private Long driverId;

    @Column(name = Constants.DriverTable.License.NAME, length = Constants.DriverTable.License.LENGTH, nullable = false)
    private String driverLicense;

    @Column(name = Constants.DriverTable.LicenseCategory.NAME, length = Constants.DriverTable.LicenseCategory.LENGTH, nullable = false)
    private String licenseCategory;

    @Column(name = Constants.DriverTable.LicenseExpiration.NAME)
    private LocalDate licenseExpiration;

    @Column(name = Constants.DriverTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.DriverTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.DriverTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.DriverTable.State.NAME, length = Constants.DriverTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.DriverTable.Id.NAME, insertable = false, updatable = false)
    private Person person;
}
