package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = Constants.User.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @Column(name = Constants.User.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = Constants.User.name.NAME, length = Constants.User.name.LENGTH, nullable = false)
    private String name;

    @Column(name =Constants.User.lastname.NAME, length = Constants.User.lastname.LENGTH, nullable = false)
    private String lastname;

    @Column(name = Constants.User.phone.NAME, nullable = true)
    private Integer phone;

    @Column(name = Constants.User.ci.NAME, length = Constants.User.ci.LENGTH, nullable = false)
    private String ci;

    @Column(name = Constants.User.role.NAME, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = Constants.User.state.NAME, nullable = false)
    private Boolean state;

    @Column(name = Constants.User.createDate.NAME, nullable = false)
    private Date createDate;




    @Column(name = Constants.User.updateDate.NAME, nullable = true)
    private Date updateDate;

    @Column(name = Constants.User.password.NAME, length = Constants.User.password.LENGTH, nullable = true)
    private String password;

    @Column(name = Constants.User.email.NAME, nullable = false)
    private String email;

    @PrePersist
    void prePersist(){
        this.createDate = new Date();
        this.state = true;
    }
    @PreUpdate
    void preUpdate(){
        this.updateDate = new Date();
    }


    @ManyToOne()
    @JoinColumn(name = Constants.User.company.NAME, referencedColumnName = Constants.Company.Id.NAME)
    private Company company;

}
