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

                        // Partners
                        .requestMatchers(HttpMethod.GET, "/api/partners", "/api/partners/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.POST, "/api/partners")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/partners/*", "/api/partners/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Drivers
                        .requestMatchers(HttpMethod.GET, "/api/drivers", "/api/drivers/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.POST, "/api/drivers")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/drivers/*", "/api/drivers/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Trucks
                        .requestMatchers(HttpMethod.GET, "/api/trucks", "/api/trucks/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER)
                        .requestMatchers(HttpMethod.POST, "/api/trucks")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/trucks/*", "/api/trucks/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Assignments
                        .requestMatchers(HttpMethod.GET, "/api/assignments", "/api/assignments/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.POST, "/api/assignments")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/assignments/*/end")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Clients, Contracts, Charges, Containers
                        .requestMatchers(HttpMethod.GET,
                                "/api/clients", "/api/clients/*",
                                "/api/contracts", "/api/contracts/*",
                                "/api/charges", "/api/charges/*",
                                "/api/containers", "/api/containers/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.POST,
                                "/api/clients", "/api/contracts",
                                "/api/charges", "/api/containers")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT,
                                "/api/clients/*", "/api/clients/*/status",
                                "/api/contracts/*", "/api/contracts/*/status",
                                "/api/charges/*", "/api/charges/*/status",
                                "/api/containers/*", "/api/containers/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Auxiliary Providers
                        .requestMatchers(HttpMethod.GET, "/api/auxiliary-providers", "/api/auxiliary-providers/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.POST, "/api/auxiliary-providers")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/auxiliary-providers/*", "/api/auxiliary-providers/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Freights
                        .requestMatchers(HttpMethod.GET, "/api/freights", "/api/freights/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER)
                        .requestMatchers(HttpMethod.POST, "/api/freights")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/freights/*", "/api/freights/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)

                        // Journeys
                        .requestMatchers(HttpMethod.GET, "/api/journeys", "/api/journeys/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.PARTNER, SecurityRoles.DRIVER)
                        .requestMatchers(HttpMethod.POST, "/api/journeys")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/journeys/*")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY)
                        .requestMatchers(HttpMethod.PUT, "/api/journeys/*/status")
                        .hasAnyRole(SecurityRoles.ADMIN, SecurityRoles.SECRETARY, SecurityRoles.DRIVER)

                        // Users and Roles
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/users/admin").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/users/*/status").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/roles").hasRole(SecurityRoles.ADMIN)
                        .requestMatchers(HttpMethod.PUT, "/api/roles/*").hasRole(SecurityRoles.ADMIN)
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
