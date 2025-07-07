package com.manager.truck.truck.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = Constants.RoleTable.NAME)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Roles {

    @Id
    @Column(name = Constants.RoleTable.Id.NAME)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = Constants.RoleTable.Role.NAME, nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = Constants.RoleTable.Description.NAME, length = Constants.RoleTable.Description.LENGTH, nullable = true)
    private String description;

}
