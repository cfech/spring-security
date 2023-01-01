package com.eazybytes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Slf4j
public class ProjectSecurityConfig {


    /**
     *
     * @param http context
     * @return a security filter chain
     * @throws Exception
     */
//    here we are overriding the default spring security configuration
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        log.debug("HELLO FROM DEFAULT SECURITY FILTER CHAIN");
        http.authorizeHttpRequests()
                //secure these paths so only authenticated users can access
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                //allow anyone to see this
                .requestMatchers("/notices", "/contact").permitAll();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }

    /**
     * could deny all requests if we wanted to
     */

//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        log.debug("HELLO FROM DEFAULT SECURITY FILTER CHAIN");
//        http.authorizeHttpRequests().anyRequest().denyAll();
//        http.formLogin();
//        http.httpBasic();
//        return http.build();
//    }

    /**
     * could also permit all requests if we wanted to
     */

//    @Bean
//    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
//        log.debug("HELLO FROM DEFAULT SECURITY FILTER CHAIN");
//        http.authorizeHttpRequests().anyRequest().permitAll();
//        http.formLogin();
//        http.httpBasic();
//        return http.build();
//    }
}
