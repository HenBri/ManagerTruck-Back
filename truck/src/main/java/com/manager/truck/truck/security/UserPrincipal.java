package com.manager.truck.truck.security;

import com.manager.truck.truck.domain.enums.UserStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String passwordHash;
    private final UserStatus status;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Long id, String email, String passwordHash, UserStatus status,
                         Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.status = status;
        this.authorities = authorities;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return status == UserStatus.ACTIVE;
    }
}
