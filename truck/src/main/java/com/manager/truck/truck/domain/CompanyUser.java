package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.MembershipStatus;
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
@Table(name = Constants.CompanyUserTable.NAME)
public class CompanyUser {

    @EmbeddedId
    private CompanyUserId id;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.CompanyUserTable.MembershipStatus.NAME, nullable = false)
    private MembershipStatus membershipStatus;

    @Column(name = Constants.CompanyUserTable.JoinedAt.NAME, insertable = false, updatable = false)
    private LocalDateTime joinedAt;
}
