package com.isamm.libraryManagement.config;

import jakarta.servlet.Filter;
import lombok.RequiredArgsConstructor;
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

    public SecurityConfiguration(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                 .authorizeHttpRequests(auth -> auth
                 .requestMatchers(
                         "/", "/home", "/home/**",
                         "/bibliotheques/**", "/ressources/**",
                         "/exemplaires", "/exemplaires/**",
                         "/api/v1/auth/**",
                         "/login", "/register",
                         "/css/**", "/js/**")
                 .permitAll()
                 .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()

                 .requestMatchers("/dashboard/**").hasAuthority("ADMIN")

                 .anyRequest().authenticated())
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll())

                // 3) Ne JAMAIS accéder directement au dossier uploads
                // .requestMatchers("/uploads/**").denyAll()
                // .requestMatchers("/ressources/**").authenticated()
                // .anyRequest().authenticated())

                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
