package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.ClientTable.NAME)
public class Client {

    @Id
    @Column(name = Constants.ClientTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

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

    @Column(name = Constants.ClientTable.TaxId.NAME, length = Constants.ClientTable.TaxId.LENGTH)
    private String taxId;

    @Column(name = Constants.ClientTable.ContactPerson.NAME, length = Constants.ClientTable.ContactPerson.LENGTH)
    private String contactPerson;

    @Column(name = Constants.ClientTable.ContactPhone.NAME, length = Constants.ClientTable.ContactPhone.LENGTH)
    private String contactPhone;

    @Column(name = Constants.ClientTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.ClientTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.ClientTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.ClientTable.State.NAME, length = Constants.ClientTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}
