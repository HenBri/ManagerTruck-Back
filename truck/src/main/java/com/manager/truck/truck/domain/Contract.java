package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = Constants.ContractTable.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Contract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Constants.ContractTable.Id.NAME)
    private Long id;
    @Column(name = Constants.ContractTable.Description.NAME, length = Constants.ContractTable.Description.LENGTH)
    private String description;
    @Column(name = Constants.ContractTable.Type.NAME, length = Constants.ContractTable.Type.LENGTH)
    private String type;
    @Column(name = Constants.ContractTable.State.NAME)
    private Boolean state;
    @Column(name = Constants.ContractTable.Date.NAME)
    @Temporal(TemporalType.DATE)
    private Date date;
    @Column(name = Constants.ContractTable.Amount.NAME)
    private Double amount;

    @PrePersist
    void prePersist(){
        this.date = new Date();
        this.state = true;
    }
}
