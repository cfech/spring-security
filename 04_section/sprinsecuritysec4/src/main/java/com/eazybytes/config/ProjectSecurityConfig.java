package com.eazybytes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.sql.DataSource;

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
        http.csrf().disable()
                .authorizeHttpRequests()
                //secure these paths so only authenticated users can access
                .requestMatchers("/myAccount", "/myBalance", "/myLoans", "/myCards").authenticated()
                //allow anyone to see this
                .requestMatchers("/notices", "/contact", "/register").permitAll();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }


    /**
     * always need this bean to tell spring security how to encode passowrd,
     * this bean tells spring how to do that
     * noOpPasswordEncoder means just leave it as plain text
     * @returns a bean of type password encoder, which is used when creating the users by the inMemoryUserDetailsManager
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // this password encoder does not actually do anything, just for testing and demonstration
        return NoOpPasswordEncoder.getInstance();
    }
}
