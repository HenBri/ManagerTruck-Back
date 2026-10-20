package com.manager.truck.truck.domain;

import com.manager.truck.truck.domain.enums.UserStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = Constants.UserTable.NAME)
public class User {

    @Id
    @Column(name = Constants.UserTable.Id.NAME, nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(name = Constants.UserTable.Email.NAME, length = Constants.UserTable.Email.LENGTH, nullable = false, unique = true)
    private String userEmail;

    @Column(name = Constants.UserTable.PasswordHash.NAME, length = Constants.UserTable.PasswordHash.LENGTH, nullable = false)
    private String passwordHash;

    @Column(name = Constants.UserTable.Name.NAME, length = Constants.UserTable.Name.LENGTH, nullable = false)
    private String userName;

    @Column(name = Constants.UserTable.Lastname.NAME, length = Constants.UserTable.Lastname.LENGTH, nullable = false)
    private String userLastname;

    @Column(name = Constants.UserTable.Ci.NAME, length = Constants.UserTable.Ci.LENGTH, nullable = true)
    private String userCi;

    @Column(name = Constants.UserTable.Phone.NAME, length = Constants.UserTable.Phone.LENGTH, nullable = true)
    private String userPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = Constants.UserTable.Status.NAME, nullable = false)
    private UserStatus userStatus;

    @Column(name = Constants.UserTable.CreatedAt.NAME, insertable = false, updatable = false)
    private LocalDateTime userCreatedAt;
}
