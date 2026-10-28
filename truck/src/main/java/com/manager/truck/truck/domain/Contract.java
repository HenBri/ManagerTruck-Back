package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.ContractState;
import com.manager.truck.truck.domain.enums.OperationType;
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
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.ContractTable.NAME)
public class Contract {

    @Id
    @Column(name = Constants.ContractTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Column(name = Constants.ContractTable.ClientId.NAME, nullable = false)
    private Long clientId;

    @Column(name = Constants.ContractTable.ContractCode.NAME, length = Constants.ContractTable.ContractCode.LENGTH, nullable = false, unique = true)
    private String contractCode;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.ContractTable.OperationType.NAME, length = Constants.ContractTable.OperationType.LENGTH, nullable = false)
    private OperationType operationType = OperationType.IMPORTACION;

    @Column(name = Constants.ContractTable.OriginLocation.NAME, length = Constants.ContractTable.OriginLocation.LENGTH)
    private String originLocation;

    @Column(name = Constants.ContractTable.DestinationLocation.NAME, length = Constants.ContractTable.DestinationLocation.LENGTH)
    private String destinationLocation;

    @Column(name = Constants.ContractTable.ExpectedStartDate.NAME)
    private LocalDate expectedStartDate;

    @Column(name = Constants.ContractTable.ExpectedDeliveryDate.NAME)
    private LocalDate expectedDeliveryDate;

    @Column(name = Constants.ContractTable.ActualDeliveryDate.NAME)
    private OffsetDateTime actualDeliveryDate;

    @Column(name = Constants.ContractTable.ReferenceDocument.NAME, length = Constants.ContractTable.ReferenceDocument.LENGTH)
    private String referenceDocument;

    @Column(name = Constants.ContractTable.DeclaredTotalWeightKg.NAME, precision = Constants.ContractTable.DeclaredTotalWeightKg.PRECISION, scale = Constants.ContractTable.DeclaredTotalWeightKg.SCALE)
    private BigDecimal declaredTotalWeightKg;

    @Column(name = Constants.ContractTable.ExpectedContainerCount.NAME)
    private Integer expectedContainerCount;

    @Column(name = Constants.ContractTable.Observations.NAME, columnDefinition = "TEXT")
    private String observations;

    @Column(name = Constants.ContractTable.ContractAmount.NAME, precision = Constants.ContractTable.ContractAmount.PRECISION, scale = Constants.ContractTable.ContractAmount.SCALE, nullable = false)
    private BigDecimal contractAmount = BigDecimal.ZERO;

    @Column(name = Constants.ContractTable.ContractDate.NAME, nullable = false)
    private LocalDate contractDate;

    @Column(name = Constants.ContractTable.ContractDescription.NAME, length = Constants.ContractTable.ContractDescription.LENGTH)
    private String contractDescription;

    @Column(name = Constants.ContractTable.ContractType.NAME, length = Constants.ContractTable.ContractType.LENGTH)
    private String contractType;

    @Column(name = Constants.ContractTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.ContractTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.ContractTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.ContractTable.State.NAME, length = Constants.ContractTable.State.LENGTH, nullable = false)
    private ContractState state = ContractState.PENDIENTE;
}
