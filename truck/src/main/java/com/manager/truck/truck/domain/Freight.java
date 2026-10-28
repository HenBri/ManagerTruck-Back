package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.FreightState;
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

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.FreightTable.NAME)
public class Freight {

    @Id
    @Column(name = Constants.FreightTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long freightId;

    @Column(name = Constants.FreightTable.ContractId.NAME, nullable = false)
    private Long contractId;

    @Column(name = Constants.FreightTable.ContainerId.NAME, nullable = false)
    private Long containerId;

    @Column(name = Constants.FreightTable.TruckId.NAME)
    private Long truckId;

    @Column(name = Constants.FreightTable.AuxiliaryProviderId.NAME)
    private Long auxiliaryProviderId;

    @Column(name = Constants.FreightTable.AuxiliaryTruckPlate.NAME, length = Constants.FreightTable.AuxiliaryTruckPlate.LENGTH)
    private String auxiliaryTruckPlate;

    @Column(name = Constants.FreightTable.FreightDescription.NAME, length = Constants.FreightTable.FreightDescription.LENGTH)
    private String freightDescription;

    @Column(name = Constants.FreightTable.FreightCode.NAME, length = Constants.FreightTable.FreightCode.LENGTH, nullable = false, unique = true)
    private String freightCode;

    @Column(name = Constants.FreightTable.ScheduledDeparture.NAME)
    private OffsetDateTime scheduledDeparture;

    @Column(name = Constants.FreightTable.ScheduledArrival.NAME)
    private OffsetDateTime scheduledArrival;

    @Column(name = Constants.FreightTable.ActualDeparture.NAME)
    private OffsetDateTime actualDeparture;

    @Column(name = Constants.FreightTable.ActualArrival.NAME)
    private OffsetDateTime actualArrival;

    @Column(name = Constants.FreightTable.DeliveryNotes.NAME, columnDefinition = "TEXT")
    private String deliveryNotes;

    @Column(name = Constants.FreightTable.FreightAmount.NAME, precision = Constants.FreightTable.FreightAmount.PRECISION, scale = Constants.FreightTable.FreightAmount.SCALE)
    private BigDecimal freightAmount;

    @Column(name = Constants.FreightTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.FreightTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.FreightTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.FreightTable.State.NAME, length = Constants.FreightTable.State.LENGTH, nullable = false)
    private FreightState state = FreightState.PENDIENTE;
}

