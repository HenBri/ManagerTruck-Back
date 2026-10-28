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
public class UserRoleId implements Serializable {

    @Column(name = Constants.UserRoleTable.UserId.NAME, nullable = false)
    private Long userId;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.UserRoleTable.RoleId.NAME, nullable = false)
    private Integer roleId;

    public UserRoleId() {
    }

    public UserRoleId(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}

