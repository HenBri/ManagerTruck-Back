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
@Table(name = Constants.RoleTable.NAME)
public class Role {

    @Id
    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.RoleTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(name = Constants.RoleTable.Name.NAME, length = Constants.RoleTable.Name.LENGTH, nullable = false, unique = true)
    private String roleName;

    @Column(name = Constants.RoleTable.Description.NAME, length = Constants.RoleTable.Description.LENGTH)
    private String roleDescription;

    @Column(name = Constants.RoleTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.RoleTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.RoleTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.RoleTable.State.NAME, length = Constants.RoleTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}
