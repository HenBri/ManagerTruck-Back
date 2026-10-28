package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class RolePermissionId implements Serializable {

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.RolePermissionTable.RoleId.NAME, nullable = false)
    private Integer roleId;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.RolePermissionTable.PermissionId.NAME, nullable = false)
    private Integer permissionId;

    public RolePermissionId() {
    }

    public RolePermissionId(Integer roleId, Integer permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }
}

