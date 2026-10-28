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
@Table(name = Constants.PersonTable.NAME)
public class Person {

    @Id
    @Column(name = Constants.PersonTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long personId;

    @Column(name = Constants.PersonTable.FirstName.NAME, length = Constants.PersonTable.FirstName.LENGTH, nullable = false)
    private String firstName;

    @Column(name = Constants.PersonTable.LastName.NAME, length = Constants.PersonTable.LastName.LENGTH, nullable = false)
    private String lastName;

    @Column(name = Constants.PersonTable.MaternalLastName.NAME, length = Constants.PersonTable.MaternalLastName.LENGTH)
    private String maternalLastName;

    @Column(name = Constants.PersonTable.Email.NAME, length = Constants.PersonTable.Email.LENGTH)
    private String email;

    @Column(name = Constants.PersonTable.Phone.NAME, length = Constants.PersonTable.Phone.LENGTH)
    private String phone;

    @Column(name = Constants.PersonTable.DocumentNumber.NAME, length = Constants.PersonTable.DocumentNumber.LENGTH)
    private String documentNumber;

    @Column(name = Constants.PersonTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.PersonTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.PersonTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.PersonTable.State.NAME, length = Constants.PersonTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}

