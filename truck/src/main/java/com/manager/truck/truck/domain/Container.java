package com.manager.truck.truck.domain;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = Constants.Container.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Container {
    @Id
    @Column(name = Constants.Container.Id.NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = Constants.Container.Code.NAME, length = Constants.Container.Code.LENGTH)
    private String code;
    @Column(name = Constants.Container.Description.NAME, length = Constants.Container.Description.LENGTH)
    private String description;
    @Column(name = Constants.Container.Type.NAME, length = Constants.Container.Type.LENGTH)
    private String type;
    @Column(name = Constants.Container.State.NAME)
    private Boolean state;
    @Column(name = Constants.Container.Date.NAME)
    private Date date;
    @Column(name = Constants.Container.Weight.NAME)
    private Double Weight;

    @ManyToOne
    @JoinColumn(name = Constants.Container.Charge.NAME, referencedColumnName = Constants.ChargeTable.Id.NAME)
    private Charge charge;

    @PrePersist
    void prePersist(){
        this.date = new Date();
        this.state = true;
    }


}
