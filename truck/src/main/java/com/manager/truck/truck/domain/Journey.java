package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.AuxiliaryReason;
import com.manager.truck.truck.domain.enums.JourneyExecutionType;
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
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.JourneyTable.NAME)
public class Journey {

    @Id
    @Column(name = Constants.JourneyTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long journeyId;

    @Column(name = Constants.JourneyTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.JourneyTable.ContainerId.NAME, nullable = false)
    private Long containerId;

    @Column(name = Constants.JourneyTable.Origin.NAME,
            length = Constants.JourneyTable.Origin.LENGTH, nullable = false)
    private String journeyOrigin;

    @Column(name = Constants.JourneyTable.Destination.NAME,
            length = Constants.JourneyTable.Destination.LENGTH, nullable = false)
    private String journeyDestination;

    @Column(name = Constants.JourneyTable.StartDate.NAME)
    private LocalDateTime journeyStartDate;

    @Column(name = Constants.JourneyTable.EndDate.NAME)
    private LocalDateTime journeyEndDate;

    @Column(name = Constants.JourneyTable.Description.NAME,
            length = Constants.JourneyTable.Description.LENGTH)
    private String journeyDescription;

    @Column(name = Constants.JourneyTable.Type.NAME,
            length = Constants.JourneyTable.Type.LENGTH)
    private String journeyType;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.JourneyTable.State.NAME, nullable = false)
    private JourneyState journeyState;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.JourneyTable.ExecutionType.NAME, nullable = false)
    private JourneyExecutionType executionType;

    @Column(name = Constants.JourneyTable.AssignmentId.NAME)
    private Long assignmentId;

    @Column(name = Constants.JourneyTable.AuxiliaryProviderId.NAME)
    private Long auxiliaryProviderId;

    @Column(name = Constants.JourneyTable.AuxiliaryTruckPlate.NAME,
            length = Constants.JourneyTable.AuxiliaryTruckPlate.LENGTH)
    private String auxiliaryTruckPlate;

    @Column(name = Constants.JourneyTable.AuxiliaryDriverName.NAME,
            length = Constants.JourneyTable.AuxiliaryDriverName.LENGTH)
    private String auxiliaryDriverName;

    @Column(name = Constants.JourneyTable.AuxiliaryDriverLicense.NAME,
            length = Constants.JourneyTable.AuxiliaryDriverLicense.LENGTH)
    private String auxiliaryDriverLicense;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.JourneyTable.AuxiliaryReason.NAME)
    private AuxiliaryReason auxiliaryReason;

    @Column(name = Constants.JourneyTable.AgreedAmount.NAME,
            precision = Constants.JourneyTable.AgreedAmount.PRECISION,
            scale = Constants.JourneyTable.AgreedAmount.SCALE)
    private BigDecimal agreedAmount;

    @Column(name = Constants.JourneyTable.CreatedAt.NAME, insertable = false, updatable = false)
    private LocalDateTime createdAt;
}
