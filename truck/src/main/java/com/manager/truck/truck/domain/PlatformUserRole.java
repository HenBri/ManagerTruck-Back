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

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.PlatformUserRoleTable.NAME)
public class PlatformUserRole {

    @EmbeddedId
    private PlatformUserRoleId id;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.PlatformUserRoleTable.RoleScope.NAME, nullable = false)
    private RoleScope roleScope;

    @Column(name = Constants.PlatformUserRoleTable.GrantedAt.NAME, insertable = false, updatable = false)
    private LocalDateTime grantedAt;
}
