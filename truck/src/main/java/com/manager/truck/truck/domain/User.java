package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.UserTable.NAME)
public class User {

    @Id
    @Column(name = Constants.UserTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = Constants.UserTable.PersonId.NAME, nullable = false, unique = true)
    private Long personId;

    @Column(name = Constants.UserTable.PasswordHash.NAME, length = Constants.UserTable.PasswordHash.LENGTH, nullable = false)
    private String passwordHash;

    @Column(name = Constants.UserTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.UserTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.UserTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.UserTable.State.NAME, length = Constants.UserTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.UserTable.PersonId.NAME, insertable = false, updatable = false)
    private Person person;
}
