package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.PartnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.PartnerTable.NAME)
public class Partner {

    @Id
    @Column(name = Constants.PartnerTable.Id.NAME, nullable = false)
    private Long partnerId;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.PartnerTable.Type.NAME, length = Constants.PartnerTable.Type.LENGTH, nullable = false)
    private PartnerType partnerType = PartnerType.PERSON;

    @Column(name = Constants.PartnerTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.PartnerTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.PartnerTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.PartnerTable.State.NAME, length = Constants.PartnerTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = Constants.PartnerTable.Id.NAME, insertable = false, updatable = false)
    private Person person;
}
