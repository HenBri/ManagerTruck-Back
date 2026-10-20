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
public class CompanyUserRoleId {

    @Column(name = Constants.CompanyUserRoleTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.CompanyUserRoleTable.UserId.NAME, nullable = false)
    private Long userId;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(name = Constants.CompanyUserRoleTable.RoleId.NAME, nullable = false)
    private Integer roleId;

    public CompanyUserRoleId() {
    }

    public CompanyUserRoleId(Long companyId, Long userId, Integer roleId) {
        this.companyId = companyId;
        this.userId = userId;
        this.roleId = roleId;
    }
}
