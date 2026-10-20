package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.RoleScope;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = Constants.CompanyUserRoleTable.NAME)
public class CompanyUserRole {

    @EmbeddedId
    private CompanyUserRoleId id;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.CompanyUserRoleTable.RoleScope.NAME, nullable = false)
    private RoleScope roleScope;
}
