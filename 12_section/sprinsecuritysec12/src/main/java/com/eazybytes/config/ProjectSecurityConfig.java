package com.eazybytes.config;

import com.eazybytes.filter.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Configuration
@Slf4j
public class ProjectSecurityConfig {


    //creating local variable to configure cors, will then feed to http
    CorsConfigurationSource corsConfigurationSource =  new CorsConfigurationSource(){
        @Override
        public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
            CorsConfiguration corsConfiguration = new CorsConfiguration();
            corsConfiguration.setAllowedOrigins(Collections.singletonList("http://localhost:4200"));
            corsConfiguration.setAllowedMethods(Collections.singletonList("*"));
            corsConfiguration.setAllowCredentials(true);
            corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
            // --- JWT --- must expose header or browser will not accept it due to cors
            corsConfiguration.setExposedHeaders(List.of("Authorization"));
            corsConfiguration.setMaxAge(3800L);

            return corsConfiguration;
        }
    };


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

        // ----------------------- CORS -----------------------
        http.cors().configurationSource(corsConfigurationSource)

                // ----------- session management ------------------------
                //spring no longer always creates a new session in spring 3, need this pre section 9 for to always create a new JSESSIONID on login
                // .and().sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.ALWAYS))

                //--- JWT --- have to set sessionManagement sessionCreationPolicy to Stateless in order to use JWT
                // --- JWT --- This turns off JSESSIONID generation
                .and().sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //-------------------- CSRF--------------------------
                //tell spring security these are public apis, but will protect all other endpoints
                //csrf process against malicious posts/puts so don't need notices as it only gets
                .csrf().ignoringRequestMatchers("/contact", "/register")

                // create a CSRF token and send it as a cookie,  and with withHttpOnlyFalse(), it allows the UI to read the cookie via JavaScript
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
                // ----------------- Fiters ------------------
                // for passing csrf cookie in the header, add it after the basicAuthenticationFilter
                .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

                //adding custom validation filter before the BasicAuthenticationFilter in the filter chain
                .addFilterBefore(new RequestValidationBeforeFilter(), BasicAuthenticationFilter.class )

                // adding custom logging filter after login
                .addFilterAfter(new AuthoritiesLoggingAfterFilter(), BasicAuthenticationFilter.class)

                // adding custom logging filter at the time of the BasicAuthenticationFilter
                .addFilterAt(new LoggingAtAuthenticationFilter(), BasicAuthenticationFilter.class)


                // --- JWT --- add filter to generate a JWT after username and password are extracted - only runs on /user (when logging in JWt is generated)
                .addFilterAfter(new JWTTokenGeneratorFilter(), BasicAuthenticationFilter.class)

                // --- JWT --- add filter to validate JWT before BasicAuthenticationFilter - runs on every authenticated route except /user (login route)
                .addFilterBefore(new JWTTokenValidatorFilter(), BasicAuthenticationFilter.class)
                //-------------------- Authentication -------------------------------------
                .authorizeHttpRequests()


                //----------------- RBAC -------------------------

                //authorities
//                .requestMatchers("/myAccount").hasAuthority("VIEWACCOUNT")
//                .requestMatchers("/myBalance").hasAnyAuthority("VIEWACCOUNT","VIEWBALANCE")
//                .requestMatchers("/myLoans").hasAuthority("VIEWLOANS")
//                .requestMatchers("/myCards").hasAuthority("VIEWCARDS")

                //roles
                .requestMatchers("/myAccount").hasRole("USER")
                .requestMatchers("/myBalance").hasAnyRole("USER","ADMIN")
                .requestMatchers("/myLoans").authenticated() // just changing to demo method security
                .requestMatchers("/myCards").hasRole("USER")
                //secure these paths so only authenticated users can access
                .requestMatchers("/user").authenticated()
                //allow anyone to see this
                .requestMatchers("/notices", "/contact", "/register").permitAll();
        // ---------------------- Login Info ------------------------------
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
//        return NoOpPasswordEncoder.getInstance();

        // now creat a BCryptPasswordEncoder, can use this in production, actually encodes the password
        return new BCryptPasswordEncoder();
    }
}
