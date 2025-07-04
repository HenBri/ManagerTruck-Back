package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
@Table(name="client")
public class Client {
    @Id
    @Column(name = Constants.ClientTable.Id.NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = Constants.ClientTable.Name.NAME, length = Constants.ClientTable.Name.LENGTH)
    private String name;
    @Column(name = Constants.ClientTable.Phone.NAME, length = Constants.ClientTable.Email.LENGTH)
    private String phone;
    @Column(name = Constants.ClientTable.Email.NAME, length = Constants.ClientTable.Email.LENGTH)
    private String email;
    @Column(name = Constants.ClientTable.Address.NAME, length = Constants.ClientTable.Address.LENGTH)
    private String address;
    @Column(name = Constants.ClientTable.State.NAME)
    private boolean state;
    @Column(name = Constants.ClientTable.Date.NAME)
    private Date date;
    @PrePersist
    void prePersist(){
        this.date =new Date();
        this.state=true;
    }

}
