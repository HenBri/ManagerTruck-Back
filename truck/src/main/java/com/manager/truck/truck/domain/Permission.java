package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.PermissionTable.NAME)
public class Permission {

    @Id
    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.PermissionTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer permissionId;

    @Column(name = Constants.PermissionTable.Code.NAME, length = Constants.PermissionTable.Code.LENGTH, nullable = false, unique = true)
    private String permissionCode;

    @Column(name = Constants.PermissionTable.Description.NAME, length = Constants.PermissionTable.Description.LENGTH)
    private String description;

    @Column(name = Constants.PermissionTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.PermissionTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.PermissionTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.PermissionTable.State.NAME, length = Constants.PermissionTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}

