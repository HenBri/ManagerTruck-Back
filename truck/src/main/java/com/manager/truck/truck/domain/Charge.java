package com.manager.truck.truck.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = Constants.ChargeTable.NAME)
public class Charge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Constants.ChargeTable.Id.NAME)
    private Long id;
    @Column(name = Constants.ChargeTable.Description.NAME, length = Constants.ChargeTable.Description.LENGTH)
    private String Description;
    @Column(name = Constants.ChargeTable.Date.NAME)
    private Date date;
    @Column(name = Constants.ChargeTable.Type.NAME)
    private String type;
    @Column(name = Constants.ChargeTable.State.NAME)
    private Boolean state;

    @PrePersist
    void prePersist(){
        this.date = new Date();
    }


}
