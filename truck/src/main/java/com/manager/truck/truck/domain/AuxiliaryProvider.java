package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.AuxiliaryProviderKind;
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

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.AuxiliaryProviderTable.NAME)
public class AuxiliaryProvider {

    @Id
    @Column(name = Constants.AuxiliaryProviderTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.AuxiliaryProviderTable.Kind.NAME, length = Constants.AuxiliaryProviderTable.Kind.LENGTH, nullable = false)
    private AuxiliaryProviderKind providerKind;

    @Column(name = Constants.AuxiliaryProviderTable.Name.NAME, length = Constants.AuxiliaryProviderTable.Name.LENGTH, nullable = false)
    private String providerName;

    @Column(name = Constants.AuxiliaryProviderTable.Document.NAME, length = Constants.AuxiliaryProviderTable.Document.LENGTH)
    private String providerDocument;

    @Column(name = Constants.AuxiliaryProviderTable.Phone.NAME, length = Constants.AuxiliaryProviderTable.Phone.LENGTH)
    private String providerPhone;

    @Column(name = Constants.AuxiliaryProviderTable.Email.NAME, length = Constants.AuxiliaryProviderTable.Email.LENGTH)
    private String providerEmail;

    @Column(name = Constants.AuxiliaryProviderTable.DateCreate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateCreate;

    @Column(name = Constants.AuxiliaryProviderTable.DateUpdate.NAME, insertable = false, updatable = false)
    private OffsetDateTime dateUpdate;

    @Column(name = Constants.AuxiliaryProviderTable.IsDelete.NAME, nullable = false)
    private Boolean isDelete = false;

    @Column(name = Constants.AuxiliaryProviderTable.State.NAME, length = Constants.AuxiliaryProviderTable.State.LENGTH, nullable = false)
    private String state = "ACTIVE";
}
