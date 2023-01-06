package com.eazybytes.config;

import com.eazybytes.filter.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class ProjectSecurityConfig {


    //creating local variable to configure cors, will then feed to http
    CorsConfigurationSource corsConfigurationSource = request -> {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
        corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
        // --- JWT --- must expose header or browser will not accept it due to cors
        corsConfiguration.setExposedHeaders(List.of("Authorization"));
        corsConfiguration.setMaxAge(3800L);

        return corsConfiguration;
    };


    /**
     * @param http context
     * @return a security filter chain
     * @throws Exception
     */
//    here we are overriding the default spring security configuration
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        log.debug("HELLO FROM DEFAULT SECURITY FILTER CHAIN");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        // set our custom implementation as the jwtAuthenticationConverter
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        // ----------------------- CORS -----------------------
        http.cors().configurationSource(corsConfigurationSource)
                // ----------- session management ------------------------
                //--- JWT --- have to set sessionManagement sessionCreationPolicy to Stateless in order to use JWT
                // --- JWT --- This turns off JSESSIONID generation
            .and().sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //-------------------- CSRF--------------------------
                //tell spring security these are public apis, but will protect all other endpoints
                //csrf process against malicious posts/puts so don't need notices as it only gets
                // create a CSRF token and send it as a cookie,  and with withHttpOnlyFalse(), it allows the UI to read the cookie via JavaScript
                .csrf().ignoringRequestMatchers("/contact", "/register").csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                // ----------------- Fiters ------------------
                // for passing csrf cookie in the header, add it after the basicAuthenticationFilter
                .and().addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .authorizeHttpRequests()
                //----------------- RBAC -------------------------
                .requestMatchers("/myAccount").hasRole("USER")
                .requestMatchers("/myCards").hasRole("USER")
                .requestMatchers("/myBalance").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/myLoans").authenticated() // just changing to demo method security
                .requestMatchers("/user").authenticated()
                .requestMatchers("/notices", "/contact", "/register").permitAll()
        // ---------------------- Login Info ------------------------------
                /// telling spring this will now act as an OAuth resource server
                // it will receive access tokens in the form of JWT's
            .and().oauth2ResourceServer().jwt().jwtAuthenticationConverter(jwtAuthenticationConverter);
        return http.build();
    }


}
