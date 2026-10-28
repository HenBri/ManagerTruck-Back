package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.JourneyState;
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
@Table(name = Constants.JourneyTable.NAME)
public class Journey {

    @Id
    @Column(name = Constants.JourneyTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long journeyId;

    @Column(name = Constants.JourneyTable.FreightId.NAME, nullable = false, unique = true)
    private Long freightId;

    @Column(name = Constants.JourneyTable.Origin.NAME, length = Constants.JourneyTable.Origin.LENGTH, nullable = false)
    private String journeyOrigin;

    @Column(name = Constants.JourneyTable.Destination.NAME, length = Constants.JourneyTable.Destination.LENGTH, nullable = false)
    private String journeyDestination;

    @Column(name = Constants.JourneyTable.StartDate.NAME)
    private OffsetDateTime journeyStartDate;

    @Column(name = Constants.JourneyTable.EndDate.NAME)
    private OffsetDateTime journeyEndDate;

    @Column(name = Constants.JourneyTable.Description.NAME, length = Constants.JourneyTable.Description.LENGTH)
    private String journeyDescription;

    @Column(name = Constants.JourneyTable.Type.NAME, length = Constants.JourneyTable.Type.LENGTH)
    private String journeyType;

    @Column(name = Constants.JourneyTable.PlannedStartDate.NAME)
    private OffsetDateTime plannedStartDate;

    @Column(name = Constants.JourneyTable.PlannedEndDate.NAME)
    private OffsetDateTime plannedEndDate;

    @Column(name = Constants.JourneyTable.DeliveryConfirmedAt.NAME)
    private OffsetDateTime deliveryConfirmedAt;

    @Column(name = Constants.JourneyTable.RecipientName.NAME, length = Constants.JourneyTable.RecipientName.LENGTH)
    private String recipientName;

    @Column(name = Constants.JourneyTable.ProofOfDeliveryReference.NAME, length = Constants.JourneyTable.ProofOfDeliveryReference.LENGTH)
    private String proofOfDeliveryReference;

    @Column(name = Constants.JourneyTable.Observations.NAME, columnDefinition = "TEXT")
    private String observations;

    @Column(name = Constants.JourneyTable.AssignmentId.NAME)
    private Long assignmentId;

    @Column(name = Constants.JourneyTable.AuxiliaryDriverName.NAME, length = Constants.JourneyTable.AuxiliaryDriverName.LENGTH)
    private String auxiliaryDriverName;

    @Column(name = Constants.JourneyTable.AuxiliaryDriverLicense.NAME, length = Constants.JourneyTable.AuxiliaryDriverLicense.LENGTH)
    private String auxiliaryDriverLicense;

    @Column(name = Constants.JourneyTable.AuxiliaryReason.NAME, length = Constants.JourneyTable.AuxiliaryReason.LENGTH)
    private String auxiliaryReason;

    @Column(name = Constants.JourneyTable.AgreedAmount.NAME, precision = Constants.JourneyTable.AgreedAmount.PRECISION, scale = Constants.JourneyTable.AgreedAmount.SCALE)
    private BigDecimal agreedAmount;

    @Column(name = Constants.JourneyTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.JourneyTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.JourneyTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.JourneyTable.State.NAME, length = Constants.JourneyTable.State.LENGTH, nullable = false)
    private JourneyState state = JourneyState.PENDIENTE;
}
