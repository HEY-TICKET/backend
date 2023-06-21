package com.heyticket.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyticket.backend.module.security.jwt.ExceptionHandlerFilter;
import com.heyticket.backend.module.security.jwt.JwtAuthenticationFilter;
import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    private final ObjectMapper objectMapper;

    @Bean
    @Profile({"!prodnoauth & !localnoauth"})
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers("/batch/**").hasRole("ADMIN")
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new ExceptionHandlerFilter(objectMapper), JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Profile({"prodnoauth", "localnoauth"})
    public SecurityFilterChain ignoreFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
            .requestMatchers("/api/members/login")
            .requestMatchers("/api/members/signup")
            .requestMatchers("/api/swagger")
            .requestMatchers("/swagger-ui/**")
            .requestMatchers("/v3/api-docs/**")
            .requestMatchers("/swagger-resources/**");
    }
}
