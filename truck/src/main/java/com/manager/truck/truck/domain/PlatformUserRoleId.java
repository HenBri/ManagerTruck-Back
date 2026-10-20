package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class PlatformUserRoleId {

    @Column(name = Constants.PlatformUserRoleTable.UserId.NAME, nullable = false)
    private Long userId;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.PlatformUserRoleTable.RoleId.NAME, nullable = false)
    private Integer roleId;

    public PlatformUserRoleId() {
    }

    public PlatformUserRoleId(Long userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }
}
