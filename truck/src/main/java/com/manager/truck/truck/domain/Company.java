package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company {

    @Id
    @Column(name = Constants.CompanyTable.Id.NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = Constants.CompanyTable.Name.NAME, length = Constants.CompanyTable.Name.LENGTH, nullable = false)
    private String name;
    @Column(name = Constants.CompanyTable.Address.NAME, length = Constants.CompanyTable.Address.LENGTH, nullable = false)
    private String address;
    @Column(name = Constants.CompanyTable.Phone.NAME, nullable = false)
    private Integer phone;
    @Column(name = Constants.CompanyTable.Email.NAME, length = Constants.CompanyTable.Email.LENGTH, nullable = true)
    private String email;
    @Column(name = Constants.CompanyTable.CreateDate.NAME, nullable = false)
    private Date createDate;
    @Column(name = Constants.CompanyTable.State.NAME, nullable = false)
    private Boolean state;

    @PrePersist
    private void prePersist(){
        this.createDate = new Date();
        this.state = true;
    }

}
