package com.manager.truck.truck.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/users")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/memberships")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.POST, "/api/companies/*/memberships")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*/memberships/*/status")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/partners",
                                "/api/companies/*/partners/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY,
                                SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/drivers",
                                "/api/companies/*/drivers/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY,
                                SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/trucks",
                                "/api/companies/*/trucks/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER)
                        .requestMatchers(HttpMethod.GET, "/api/companies/*/assignments",
                                "/api/companies/*/assignments/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY,
                                SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.GET,
                                "/api/companies/*/clients", "/api/companies/*/clients/*",
                                "/api/companies/*/contracts", "/api/companies/*/contracts/*",
                                "/api/companies/*/charges", "/api/companies/*/charges/*",
                                "/api/companies/*/containers", "/api/companies/*/containers/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.POST,
                                "/api/companies/*/clients", "/api/companies/*/contracts",
                                "/api/companies/*/charges", "/api/companies/*/containers")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT,
                                "/api/companies/*/clients/*", "/api/companies/*/clients/*/status",
                                "/api/companies/*/contracts/*", "/api/companies/*/contracts/*/status",
                                "/api/companies/*/charges/*", "/api/companies/*/charges/*/status",
                                "/api/companies/*/containers/*", "/api/companies/*/containers/*/status")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.GET,
                                "/api/companies/*/auxiliary-providers", "/api/companies/*/auxiliary-providers/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.POST, "/api/companies/*/auxiliary-providers")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT,
                                "/api/companies/*/auxiliary-providers/*",
                                "/api/companies/*/auxiliary-providers/*/status")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.GET,
                                "/api/companies/*/journeys", "/api/companies/*/journeys/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY,
                                SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.POST, "/api/companies/*/journeys")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*/journeys/*")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*/journeys/*/status")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY,
                                SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.POST, "/api/companies/*/partners",
                                "/api/companies/*/drivers", "/api/companies/*/trucks",
                                "/api/companies/*/assignments")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*/partners/*",
                                "/api/companies/*/partners/*/status",
                                "/api/companies/*/drivers/*", "/api/companies/*/drivers/*/status",
                                "/api/companies/*/trucks/*", "/api/companies/*/trucks/*/status",
                                "/api/companies/*/assignments/*/end")
                        .hasAnyRole(SecurityRoles.SUPER_ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.GET, "/api/platform/user-roles")
                        .hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/companies").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/companies").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*/status").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/companies/*").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/users/admin").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/status").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/roles").hasRole(SecurityRoles.SUPER_ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*").hasRole(SecurityRoles.SUPER_ADMIN)
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(jsonAuthenticationEntryPoint()))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jsonAuthenticationEntryPoint())
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"code\":\"403\",\"message\":\"FORBIDDEN\"}");
                        }));
        return http.build();
    }

    private static org.springframework.security.web.AuthenticationEntryPoint jsonAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":\"401\",\"message\":\"UNAUTHORIZED\"}");
        };
    }
}
