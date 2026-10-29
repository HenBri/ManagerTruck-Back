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
@Table(name = "company")
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "company_name", nullable = false, length = 150)
    private String companyName;

    @Column(name = "company_nit", length = 30)
    private String companyNit;

    @Column(name = "company_address", length = 255)
    private String companyAddress;

    @Column(name = "company_phone", length = 25)
    private String companyPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "company_status", nullable = false)
    private CompanyStatus companyStatus = CompanyStatus.ACTIVE;

    @Column(name = "company_created_at", insertable = false, updatable = false)
    private LocalDateTime companyCreatedAt;
}
