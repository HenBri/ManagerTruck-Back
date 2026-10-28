package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.ChargeTable.NAME)
public class Charge {

    @Id
    @Column(name = Constants.ChargeTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargeId;

    @Column(name = Constants.ChargeTable.ContractId.NAME, nullable = false)
    private Long contractId;

    @Column(name = Constants.ChargeTable.Description.NAME, length = Constants.ChargeTable.Description.LENGTH)
    private String chargeDescription;

    @Column(name = Constants.ChargeTable.Date.NAME, insertable = false, updatable = false)
    private OffsetDateTime chargeDate;

    @Column(name = Constants.ChargeTable.Type.NAME, length = Constants.ChargeTable.Type.LENGTH)
    private String chargeType;

    @Column(name = Constants.ChargeTable.CargoReference.NAME, length = Constants.ChargeTable.CargoReference.LENGTH)
    private String cargoReference;

    @Column(name = Constants.ChargeTable.GoodsDescription.NAME, columnDefinition = "TEXT")
    private String goodsDescription;

    @Column(name = Constants.ChargeTable.HazardousGoods.NAME, nullable = false)
    private Boolean hazardousGoods = false;

    @Column(name = Constants.ChargeTable.HandlingInstructions.NAME, columnDefinition = "TEXT")
    private String handlingInstructions;

    @Column(name = Constants.ChargeTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.ChargeTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.ChargeTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.ChargeTable.State.NAME, length = Constants.ChargeTable.State.LENGTH, nullable = false)
    private String state = "PENDING";
}
