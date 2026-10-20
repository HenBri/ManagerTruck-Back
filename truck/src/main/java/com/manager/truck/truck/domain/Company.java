package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.CompanyStatus;
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
@Table(name = Constants.CompanyTable.NAME)
public class Company {

    @Id
    @Column(name = Constants.CompanyTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long companyId;

    @Column(name = Constants.CompanyTable.Name.NAME, length = Constants.CompanyTable.Name.LENGTH, nullable = false)
    private String companyName;

    @Column(name = Constants.CompanyTable.Nit.NAME, length = Constants.CompanyTable.Nit.LENGTH, nullable = true)
    private String companyNit;

    @Column(name = Constants.CompanyTable.Address.NAME, length = Constants.CompanyTable.Address.LENGTH, nullable = true)
    private String companyAddress;

    @Column(name = Constants.CompanyTable.Phone.NAME, length = Constants.CompanyTable.Phone.LENGTH, nullable = true)
    private String companyPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.CompanyTable.Status.NAME, nullable = false)
    private CompanyStatus companyStatus;

    @Column(name = Constants.CompanyTable.CreatedAt.NAME, insertable = false, updatable = false)
    private LocalDateTime companyCreatedAt;
}
