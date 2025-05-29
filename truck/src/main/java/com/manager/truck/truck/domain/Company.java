package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebProperties;

import java.util.Date;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    @Id
    @Column(name = Constants.Company.Id.NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = Constants.Company.name.NAME, length = Constants.Company.name.LENGTH, nullable = false)
    private String name;
    @Column(name = Constants.Company.address.NAME, length = Constants.Company.address.LENGTH, nullable = false)
    private String address;
    @Column(name = Constants.Company.phone.NAME, nullable = false)
    private Integer phone;
    @Column(name = Constants.Company.email.NAME, length = Constants.Company.email.LENGTH, nullable = true)
    private String email;
    @Column(name = Constants.Company.createDate.NAME, nullable = false)
    private Date createDate;
    @Column(name = Constants.Company.state.NAME, nullable = false)
    private Boolean state;

    @PrePersist
    private void prePersist(){
        this.createDate = new Date();
        this.state = true;
    }

}
