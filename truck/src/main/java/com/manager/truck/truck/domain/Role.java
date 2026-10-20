package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.RoleScope;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import lombok.Getter;
import lombok.Setter;

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

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.RoleTable.Scope.NAME, nullable = false)
    private RoleScope roleScope;

    @Column(name = Constants.RoleTable.Description.NAME, length = Constants.RoleTable.Description.LENGTH, nullable = true)
    private String roleDescription;
}
