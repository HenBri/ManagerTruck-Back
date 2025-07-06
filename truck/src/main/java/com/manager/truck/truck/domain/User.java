package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = Constants.UserTable.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {

    @Id
    @Column(name = Constants.UserTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(name = Constants.UserTable.Name.NAME, length = Constants.UserTable.Name.LENGTH, nullable = false)
    private String name;

    @Column(name = Constants.UserTable.Lastname.NAME, length = Constants.UserTable.Lastname.LENGTH, nullable = false)
    private String lastname;

    @Column(name = Constants.UserTable.Phone.NAME, nullable = true)
    private Integer phone;

    @Column(name = Constants.UserTable.Ci.NAME, length = Constants.UserTable.Ci.LENGTH, nullable = false)
    private String ci;

    @Column(name = Constants.UserTable.State.NAME, nullable = false)
    private Boolean state;

    @Column(name = Constants.UserTable.CreateDate.NAME, nullable = false)
    private Date createDate;

    @Column(name = Constants.UserTable.UpdateDate.NAME, nullable = true)
    private Date updateDate;

    @Column(name = Constants.UserTable.Password.NAME, length = Constants.UserTable.Password.LENGTH, nullable = true)
    private String password;

    @Column(name = Constants.UserTable.Email.NAME, nullable = false)
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
    @JoinColumn(name = Constants.UserTable.Company.NAME, referencedColumnName = Constants.CompanyTable.Id.NAME)
    private Company company;

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = Constants.UserTable.Id.NAME, referencedColumnName = Constants.UserTable.Id.NAME),
            inverseJoinColumns = @JoinColumn(name = Constants.RoleTable.Id.NAME, referencedColumnName = Constants.RoleTable.Id.NAME))
    private Set<Roles> roles = new HashSet<>();


}
