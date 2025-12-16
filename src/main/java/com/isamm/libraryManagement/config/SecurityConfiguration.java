package com.isamm.libraryManagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter,
                                 AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                // PUBLIC
                .requestMatchers(
                        "/", "/home", "/home/**",
                        "/login", "/register",
                        "/api/v1/auth/**",
                        "/css/**", "/js/**"
                ).permitAll()

                // Si tu veux rendre ces modules publics comme tu avais fait :
                .requestMatchers(
                        "/bibliotheques/**", "/ressources/**",
                        "/exemplaires", "/exemplaires/**",
                        "/loans/**", "/api/loans/**",
                        "/notifications/**"
                ).permitAll()

                // Dashboard: accessible aux 3 rôles
                .requestMatchers("/dashboard", "/dashboard/**")
                .hasAnyAuthority("ADMIN", "USER", "BIBLIOTHECAIRE")

                // Export: ADMIN uniquement
                .requestMatchers("/export/**").hasAuthority("ADMIN")

                // FIN: tout le reste doit être authentifié
                .anyRequest().authenticated()
        );

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));

        http.authenticationProvider(authenticationProvider);

        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
