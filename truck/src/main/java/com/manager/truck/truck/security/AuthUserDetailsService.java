package com.manager.truck.truck.security;

import com.manager.truck.truck.domain.User;
import com.manager.truck.truck.repository.UserRepository;
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

    private final UserRepository userRepository;

    @Override
    public UserPrincipal loadUserByUsername(String email) {
        User user = userRepository.findByPersonEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        Set<SimpleGrantedAuthority> authorities = new LinkedHashSet<>();
        userRepository.findRoleNames(user.getUserId())
                .forEach(role -> authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role)));

        String userEmail = (user.getPerson() != null && user.getPerson().getEmail() != null)
                ? user.getPerson().getEmail()
                : email;

        return new UserPrincipal(user.getUserId(), userEmail, user.getPasswordHash(),
                user.getState(), authorities);
    }
}
