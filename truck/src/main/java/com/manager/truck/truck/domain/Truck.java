package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = Constants.TruckTable.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Truck {


    @Id
    @Column(name= Constants.TruckTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


    @Column(name = Constants.TruckTable.Plate.NAME, length = Constants.TruckTable.Plate.LENGTH, nullable = false)
    private String plate;
    @Column(name = Constants.TruckTable.Model.NAME, length = Constants.TruckTable.Model.LENGTH, nullable = false)
    private String model;
    @Column(name = Constants.TruckTable.Color.NAME, length = Constants.TruckTable.Color.LENGTH, nullable = true)
    private String color;
    @Column(name = Constants.TruckTable.Brand.NAME, length = Constants.TruckTable.Brand.LENGTH, nullable = false)
    private String brand;
    @Column(name = Constants.TruckTable.Chassis.NAME, length = Constants.TruckTable.Chassis.LENGTH, nullable = true)
    private String chassis;
    @Column(name= Constants.TruckTable.State.NAME, nullable = false)
    private Boolean state;

    @Column(name = Constants.TruckTable.CreateDate.NAME, nullable = false)
    private Date crateDate;

    @ManyToOne
    @JoinColumn(name = Constants.TruckTable.User.NAME,referencedColumnName = Constants.UserTable.Id.NAME)
    private User user;

    @PrePersist()
    public void onPersist(){
         this.crateDate = new Date();
         this.state = true;
    }

}
