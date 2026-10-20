package com.manager.truck.truck.security;

import com.manager.truck.truck.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private static final String ROLE_PREFIX = "ROLE_";

    private final UserService userService;

    @Override
    public UserPrincipal loadUserByUsername(String email) {
        return userService.findByEmail(email)
                .map(user -> {
                    Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
                    userService.findPlatformRoleNames(user.getUserId())
                            .forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
                    userService.findAllCompanyRoleNames(user.getUserId())
                            .forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));
                    return new UserPrincipal(user.getUserId(), user.getUserEmail(), user.getPasswordHash(),
                            user.getUserStatus(), authorities);
                })
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}
