package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.ContainerTable.NAME)
public class Container {

    @Id
    @Column(name = Constants.ContainerTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long containerId;

    @Column(name = Constants.ContainerTable.ChargeId.NAME, nullable = false)
    private Long chargeId;

    @Column(name = Constants.ContainerTable.Code.NAME, length = Constants.ContainerTable.Code.LENGTH, nullable = false)
    private String containerCode;

    @Column(name = Constants.ContainerTable.Weight.NAME, precision = Constants.ContainerTable.Weight.PRECISION, scale = Constants.ContainerTable.Weight.SCALE)
    private BigDecimal containerWeight;

    @Column(name = Constants.ContainerTable.Description.NAME, length = Constants.ContainerTable.Description.LENGTH)
    private String containerDescription;

    @Column(name = Constants.ContainerTable.Type.NAME, length = Constants.ContainerTable.Type.LENGTH)
    private String containerType;

    @Column(name = Constants.ContainerTable.IsoSize.NAME, length = Constants.ContainerTable.IsoSize.LENGTH)
    private String isoSize;

    @Column(name = Constants.ContainerTable.ContainerFormat.NAME, length = Constants.ContainerTable.ContainerFormat.LENGTH)
    private String containerFormat;

    @Column(name = Constants.ContainerTable.SealNumber.NAME, length = Constants.ContainerTable.SealNumber.LENGTH)
    private String sealNumber;

    @Column(name = Constants.ContainerTable.GrossWeightKg.NAME, precision = Constants.ContainerTable.GrossWeightKg.PRECISION, scale = Constants.ContainerTable.GrossWeightKg.SCALE)
    private BigDecimal grossWeightKg;

    @Column(name = Constants.ContainerTable.TareWeightKg.NAME, precision = Constants.ContainerTable.TareWeightKg.PRECISION, scale = Constants.ContainerTable.TareWeightKg.SCALE)
    private BigDecimal tareWeightKg;

    @Column(name = Constants.ContainerTable.CargoWeightKg.NAME, precision = Constants.ContainerTable.CargoWeightKg.PRECISION, scale = Constants.ContainerTable.CargoWeightKg.SCALE)
    private BigDecimal cargoWeightKg;

    @Column(name = Constants.ContainerTable.ShippingLine.NAME, length = Constants.ContainerTable.ShippingLine.LENGTH)
    private String shippingLine;

    @Column(name = Constants.ContainerTable.CustomsReference.NAME, length = Constants.ContainerTable.CustomsReference.LENGTH)
    private String customsReference;

    @Column(name = Constants.ContainerTable.TemperatureC.NAME, precision = Constants.ContainerTable.TemperatureC.PRECISION, scale = Constants.ContainerTable.TemperatureC.SCALE)
    private BigDecimal temperatureC;

    @Column(name = Constants.ContainerTable.ContainerDate.NAME, insertable = false, updatable = false)
    private OffsetDateTime containerDate;

    @Column(name = Constants.ContainerTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.ContainerTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.ContainerTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.ContainerTable.State.NAME, length = Constants.ContainerTable.State.LENGTH, nullable = false)
    private String state = "AVAILABLE";
}
