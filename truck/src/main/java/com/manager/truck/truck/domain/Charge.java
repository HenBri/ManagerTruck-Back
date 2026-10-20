package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.ChargeState;
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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.ChargeTable.NAME)
public class Charge {

    @Id
    @Column(name = Constants.ChargeTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long chargeId;

    @Column(name = Constants.ChargeTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.ChargeTable.ContractId.NAME, nullable = false)
    private Long contractId;

    @Column(name = Constants.ChargeTable.Description.NAME, length = Constants.ChargeTable.Description.LENGTH)
    private String chargeDescription;

    @Column(name = Constants.ChargeTable.Date.NAME, insertable = false, updatable = false)
    private LocalDateTime chargeDate;

    @Column(name = Constants.ChargeTable.Type.NAME, length = Constants.ChargeTable.Type.LENGTH)
    private String chargeType;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.ChargeTable.State.NAME, nullable = false)
    private ChargeState chargeState;
}
