package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = Constants.ClientTable.NAME)
public class Client {

    @Id
    @Column(name = Constants.ClientTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @Column(name = Constants.ClientTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.ClientTable.Name.NAME, length = Constants.ClientTable.Name.LENGTH, nullable = false)
    private String clientName;

    @Column(name = Constants.ClientTable.Ci.NAME, length = Constants.ClientTable.Ci.LENGTH)
    private String clientCi;

    @Column(name = Constants.ClientTable.Email.NAME, length = Constants.ClientTable.Email.LENGTH)
    private String clientEmail;

    @Column(name = Constants.ClientTable.Phone.NAME, length = Constants.ClientTable.Phone.LENGTH)
    private String clientPhone;

    @Column(name = Constants.ClientTable.Address.NAME, length = Constants.ClientTable.Address.LENGTH)
    private String clientAddress;

    @Column(name = Constants.ClientTable.State.NAME, nullable = false)
    private Boolean clientState;

    @Column(name = Constants.ClientTable.CreateDate.NAME, insertable = false, updatable = false)
    private LocalDateTime clientCreateDate;
}
