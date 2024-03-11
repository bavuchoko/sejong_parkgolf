package com.pjs.golf.config;

import com.pjs.golf.config.filter.TokenAccessDeniedHandler;
import com.pjs.golf.config.filter.TokenAuthenticationEntryPoint;
import com.pjs.golf.config.token.TokenManager;
import com.pjs.golf.config.token.TokenSecurityConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsUtils;


@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final TokenManager tokenManager;
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;


    public SecurityConfig(TokenManager tokenManager, TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint, TokenAccessDeniedHandler tokenAccessDeniedHandler) {
        this.tokenManager = tokenManager;
        this.tokenAuthenticationEntryPoint = tokenAuthenticationEntryPoint;
        this.tokenAccessDeniedHandler = tokenAccessDeniedHandler;
    }



    //인증서버 설정시 시큐리시 설정에 SecurityFilterChain 이 있으면 둘중 하나 선택하라고 에러남
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(tokenAuthenticationEntryPoint)
                .accessDeniedHandler(tokenAccessDeniedHandler)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/**").permitAll()
                .antMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .apply(new TokenSecurityConfig(tokenManager))

                .and().build();
    }

}
