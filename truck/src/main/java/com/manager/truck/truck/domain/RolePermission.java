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
@Table(name = Constants.RolePermissionTable.NAME)
public class RolePermission {

    @EmbeddedId
    private RolePermissionId id;

    @Column(name = Constants.RolePermissionTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.RolePermissionTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.RolePermissionTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.RolePermissionTable.State.NAME, length = Constants.RolePermissionTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}

