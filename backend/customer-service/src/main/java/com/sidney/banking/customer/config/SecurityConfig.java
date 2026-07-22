package com.sidney.banking.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/actuator/health",
                                        "/actuator/info"
                                )
                                .permitAll()

                                .requestMatchers("/api/customers/**")
                                .authenticated()

                                .anyRequest()
                                .authenticated()
                )

                .oauth2ResourceServer(resourceServer ->
                        resourceServer.jwt(Customizer.withDefaults())
                )

                .build();
    }
}