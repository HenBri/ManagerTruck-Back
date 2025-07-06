package com.manager.truck.truck.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
public class Container {
    @Id
    @Column(name = Constants.Container.NAME)
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

    @PrePersist
    void prePersist(){
        this.date = new Date();
        this.state = true;
    }


}
