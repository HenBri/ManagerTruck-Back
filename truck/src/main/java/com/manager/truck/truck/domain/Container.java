package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.ContainerState;
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
@Table(name = Constants.ContainerTable.NAME)
public class Container {

    @Id
    @Column(name = Constants.ContainerTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long containerId;

    @Column(name = Constants.ContainerTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.ContainerTable.ChargeId.NAME, nullable = false)
    private Long chargeId;

    @Column(name = Constants.ContainerTable.Code.NAME, length = Constants.ContainerTable.Code.LENGTH, nullable = false)
    private String containerCode;

    @Column(name = Constants.ContainerTable.Weight.NAME,
            precision = Constants.ContainerTable.Weight.PRECISION,
            scale = Constants.ContainerTable.Weight.SCALE)
    private BigDecimal containerWeight;

    @Column(name = Constants.ContainerTable.Description.NAME, length = Constants.ContainerTable.Description.LENGTH)
    private String containerDescription;

    @Column(name = Constants.ContainerTable.Type.NAME, length = Constants.ContainerTable.Type.LENGTH)
    private String containerType;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.ContainerTable.State.NAME, nullable = false)
    private ContainerState containerState;

    @Column(name = Constants.ContainerTable.Date.NAME, insertable = false, updatable = false)
    private LocalDateTime containerDate;
}
