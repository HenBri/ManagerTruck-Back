package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.TruckTable.NAME)
public class Truck {

    @Id
    @Column(name = Constants.TruckTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long truckId;

    @Column(name = Constants.TruckTable.OwnerPartnerId.NAME, nullable = false)
    private Long ownerPartnerId;

    @Column(name = Constants.TruckTable.Board.NAME, length = Constants.TruckTable.Board.LENGTH, nullable = false, unique = true)
    private String truckBoard;

    @Column(name = Constants.TruckTable.Brand.NAME, length = Constants.TruckTable.Brand.LENGTH)
    private String truckBrand;

    @Column(name = Constants.TruckTable.Model.NAME, length = Constants.TruckTable.Model.LENGTH)
    private String truckModel;

    @Column(name = Constants.TruckTable.Chassis.NAME, length = Constants.TruckTable.Chassis.LENGTH)
    private String truckChassis;

    @Column(name = Constants.TruckTable.Color.NAME, length = Constants.TruckTable.Color.LENGTH)
    private String truckColor;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.TruckTable.Year.NAME)
    private Integer truckYear;

    @Column(name = Constants.TruckTable.Capacity.NAME, precision = Constants.TruckTable.Capacity.PRECISION, scale = Constants.TruckTable.Capacity.SCALE)
    private BigDecimal truckCapacity;

    @Column(name = Constants.TruckTable.EmptyWeight.NAME, precision = Constants.TruckTable.EmptyWeight.PRECISION, scale = Constants.TruckTable.EmptyWeight.SCALE)
    private BigDecimal truckEmptyWeightKg;

    @Column(name = Constants.TruckTable.TrailerEmptyWeight.NAME, precision = Constants.TruckTable.TrailerEmptyWeight.PRECISION, scale = Constants.TruckTable.TrailerEmptyWeight.SCALE)
    private BigDecimal trailerEmptyWeightKg;

    @Column(name = Constants.TruckTable.MaxGrossWeight.NAME, precision = Constants.TruckTable.MaxGrossWeight.PRECISION, scale = Constants.TruckTable.MaxGrossWeight.SCALE)
    private BigDecimal maxGrossWeightKg;

    @Column(name = Constants.TruckTable.Type.NAME, length = Constants.TruckTable.Type.LENGTH)
    private String truckType;

    @Column(name = Constants.TruckTable.ChassisPlate.NAME, length = Constants.TruckTable.ChassisPlate.LENGTH)
    private String chassisPlate;

    @Column(name = Constants.TruckTable.InsuranceExpiration.NAME)
    private LocalDate insuranceExpiration;

    @Column(name = Constants.TruckTable.TechnicalInspectionExpiration.NAME)
    private LocalDate technicalInspectionExpiration;

    @Column(name = Constants.TruckTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.TruckTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.TruckTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.TruckTable.State.NAME, length = Constants.TruckTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}
