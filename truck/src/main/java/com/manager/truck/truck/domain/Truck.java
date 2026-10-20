package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.TruckStatus;
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
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.TruckTable.NAME)
public class Truck {

    @Id
    @Column(name = Constants.TruckTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long truckId;

    @Column(name = Constants.TruckTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.TruckTable.OwnerPartnerId.NAME, nullable = false)
    private Long ownerPartnerId;

    @Column(name = Constants.TruckTable.Board.NAME, length = Constants.TruckTable.Board.LENGTH, nullable = false)
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

    @Column(name = Constants.TruckTable.Capacity.NAME,
            precision = Constants.TruckTable.Capacity.PRECISION,
            scale = Constants.TruckTable.Capacity.SCALE)
    private BigDecimal truckCapacity;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.TruckTable.State.NAME, nullable = false)
    private TruckStatus truckState;

    @Column(name = Constants.TruckTable.CreateDate.NAME, insertable = false, updatable = false)
    private LocalDateTime truckCreateDate;
}
