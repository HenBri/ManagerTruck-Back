package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.PartnerStatus;
import com.manager.truck.truck.domain.enums.PartnerType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = Constants.PartnerTable.NAME)
public class Partner {

    @Id
    @Column(name = Constants.PartnerTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long partnerId;

    @Column(name = Constants.PartnerTable.CompanyId.NAME, nullable = false)
    private Long companyId;

    @Column(name = Constants.PartnerTable.UserId.NAME)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.PartnerTable.Type.NAME, nullable = false)
    private PartnerType partnerType;

    @Column(name = Constants.PartnerTable.Name.NAME, length = Constants.PartnerTable.Name.LENGTH, nullable = false)
    private String partnerName;

    @Column(name = Constants.PartnerTable.Document.NAME, length = Constants.PartnerTable.Document.LENGTH)
    private String partnerDocument;

    @Column(name = Constants.PartnerTable.Phone.NAME, length = Constants.PartnerTable.Phone.LENGTH)
    private String partnerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.PartnerTable.State.NAME, nullable = false)
    private PartnerStatus partnerState;
}
