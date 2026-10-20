package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.ContractState;
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

@Getter
@Setter
@Entity
@Table(name = Constants.ContractTable.NAME)
public class Contract {

    @Id
    @Column(name = Constants.ContractTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contractId;

    @Column(name = Constants.ContractTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.ContractTable.ClientId.NAME, nullable = false)
    private Long clientId;

    @Column(name = Constants.ContractTable.Amount.NAME,
            precision = Constants.ContractTable.Amount.PRECISION,
            scale = Constants.ContractTable.Amount.SCALE,
            nullable = false)
    private BigDecimal contractAmount;

    @Column(name = Constants.ContractTable.Date.NAME, nullable = false)
    private LocalDate contractDate;

    @Column(name = Constants.ContractTable.Description.NAME, length = Constants.ContractTable.Description.LENGTH)
    private String contractDescription;

    @Column(name = Constants.ContractTable.Type.NAME, length = Constants.ContractTable.Type.LENGTH)
    private String contractType;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.ContractTable.State.NAME, nullable = false)
    private ContractState contractState;
}
