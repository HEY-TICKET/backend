package com.heyticket.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyticket.backend.module.security.jwt.ExceptionHandlerFilter;
import com.heyticket.backend.module.security.jwt.JwtAuthenticationFilter;
import com.heyticket.backend.module.security.jwt.JwtTokenProvider;
import com.heyticket.backend.module.security.oauth.MyAuthenticationFailureHandler;
import com.heyticket.backend.module.security.oauth.MyAuthenticationSuccessHandler;
import com.heyticket.backend.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

    private final CustomOAuth2UserService customOAuth2UserService;

    private final MyAuthenticationSuccessHandler authenticationSuccessHandler;

    private final MyAuthenticationFailureHandler authenticationFailureHandler;

    @Bean
    @Profile({"prodnoauth", "localnoauth", "test"})
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
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .httpBasic().disable()
            .formLogin().disable()
            .oauth2Login()
            .successHandler(authenticationSuccessHandler)
            .failureHandler(authenticationFailureHandler)
            .userInfoEndpoint()
            .userService(customOAuth2UserService);

        http
            .authorizeHttpRequests(authorize ->
                authorize
                    .requestMatchers("/batch/**").hasRole("ADMIN")
                    .requestMatchers("/api/members/login").permitAll()
                    .requestMatchers("/api/members/signup").permitAll()
                    .requestMatchers("/api/members/token").permitAll()
                    .requestMatchers("/api/members/password/reset").permitAll()
                    .requestMatchers("/api/members/verification/verify").permitAll()
                    .requestMatchers("/api/members/verification/send").permitAll()
                    .requestMatchers("/api/members/validation").permitAll()
                    .requestMatchers("/api/**").authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new ExceptionHandlerFilter(objectMapper), JwtAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
            .requestMatchers("/api/performances/**")
            .requestMatchers("/api/swagger")
            .requestMatchers("/swagger-ui/**")
            .requestMatchers("/v3/api-docs/**")
            .requestMatchers("/swagger-resources/**");
    }
}
