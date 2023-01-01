package com.eazybytes.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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
     * creating in memory user approach 1
     */
//    @Bean
//    public InMemoryUserDetailsManager userDetailsService(){
//        //User is a class provided by spring security, it implements UserDetails
//        UserDetails admin = User.withDefaultPasswordEncoder()
//                .username("admin")
//                .password("12345")
//                .authorities("admin")
//                .roles("admin")
//                .build();
//        UserDetails user = User.withDefaultPasswordEncoder()
//                .username ("user")
//                .password("12345")
//                .authorities("read")
//                .roles("read")
//                .build();
//
//        //pass users to the InMemoryUserDetailsManager constructor, which creates the users
//        return new InMemoryUserDetailsManager (admin, user);
//    }

    /**
     * creating in memory user approach 2
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService(){
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        UserDetails admin = User.withUsername("admin").password ("12348").authorities("admin").build();
        UserDetails user = User.withUsername("user").password("12348").authorities("read").build();
        inMemoryUserDetailsManager.createUser(admin);
        inMemoryUserDetailsManager.createUser(user);
        return inMemoryUserDetailsManager;
    }


    /**
     *
     * @returns a bean of type password encoder, which is used when creating the users by the inMemoryUserDetailsManager
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        // this password encoder does not actually do anything, just for testing and demonstration
        return NoOpPasswordEncoder.getInstance();
    }
}
