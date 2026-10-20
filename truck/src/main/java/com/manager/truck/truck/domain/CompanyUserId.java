package com.manager.truck.truck.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class CompanyUserId {

    @Column(name = Constants.CompanyUserTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.CompanyUserTable.UserId.NAME, nullable = false)
    private Long userId;

    public CompanyUserId() {
    }

    public CompanyUserId(Long companyId, Long userId) {
        this.companyId = companyId;
        this.userId = userId;
    }
}
