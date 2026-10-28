package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.UserRoleTable.NAME)
public class UserRole {

    @EmbeddedId
    private UserRoleId id;

    @Column(name = Constants.UserRoleTable.GrantedAt.NAME, insertable = false, updatable = false)
    private OffsetDateTime grantedAt;

    @Column(name = Constants.UserRoleTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.UserRoleTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.UserRoleTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.UserRoleTable.State.NAME, length = Constants.UserRoleTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}

