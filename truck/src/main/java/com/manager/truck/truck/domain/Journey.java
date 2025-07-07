package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = Constants.JourneyTable.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Journey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = Constants.JourneyTable.Id.NAME)
    private Long id;
    @Column(name = Constants.JourneyTable.Description.NAME, length = Constants.JourneyTable.Description.LENGTH)
    private String description;
    @Column(name = Constants.JourneyTable.Origin.NAME, length = Constants.JourneyTable.Origin.LENGTH)
    private String origin;
    @Column(name = Constants.JourneyTable.Destination.NAME, length = Constants.JourneyTable.Destination.LENGTH)
    private String destination;
    @Column(name = Constants.JourneyTable.DateStart.NAME)
    @Temporal(TemporalType.DATE)
    private Date dateStart;
    @Column(name = Constants.JourneyTable.DateEnd.NAME)
    @Temporal(TemporalType.DATE)
    private Date dateEnd;
    @Column(name = Constants.JourneyTable.StateJourney.NAME)
    private String stateJourney;
    @Column(name = Constants.JourneyTable.State.NAME)
    private Boolean state;
    @Column(name = Constants.JourneyTable.Type.NAME, length = Constants.JourneyTable.Type.LENGTH)
    private String type;

    @OneToOne
    @JoinColumn(name = Constants.JourneyTable.Container.NAME, referencedColumnName = Constants.Container.Id.NAME)
    private Container container;


    @ManyToOne
    @JoinColumn(name = Constants.JourneyTable.Truck.NAME, referencedColumnName = Constants.TruckTable.Id.NAME)
    private Truck truck;

    @ManyToOne
    @JoinColumn(name = Constants.JourneyTable.Contract.NAME, referencedColumnName = Constants.ContractTable.Id.NAME)
    private Contract contract;

    @ManyToOne
    @JoinColumn(name = Constants.JourneyTable.User.NAME, referencedColumnName = Constants.UserTable.Id.NAME)
    private User user;




    @PrePersist
    void prePersist(){
        this.state = true;
        this.dateStart = new Date();
    }
}
