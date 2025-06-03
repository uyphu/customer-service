package com.tcs.assignment.customer.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     http
    //         .csrf(csrf -> csrf.disable())
    //         .authorizeHttpRequests(auth -> auth
    //             .requestMatchers(
    //                 "/swagger-ui/**",
    //                 "/v3/api-docs/**",
    //                 "/swagger-resources/**"
    //             ).permitAll()
    //             .anyRequest().authenticated()
    //         )
    //         .httpBasic(Customizer.withDefaults()); // ✅ New way

    //     return http.build();
    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**"
                ).permitAll()
                .anyRequest().permitAll()
            )
            .httpBasic(Customizer.withDefaults()); // If you're using basic auth

        return http.build();
    }
}
