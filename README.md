# Spring-Security

[For spring security zero to master](https://www.udemy.com/course/spring-security-zero-to-master/?src=sac&kw=spring+security+zero+to+master)

[Course github](https://github.com/eazybytes/springsecurity6)


# Section 1 #

## Creating spring projects ##
can create a spring project at https://start.spring.io/
- most basic project uses spring web (web server default is tomcat), and dev tools
- can add in spring security for security starter

Diff with vs without spring security
![spring security](./images/diff_with_spring_security.png)

## Spring Security Out Of The Box ##
- as soon as you add the spring security dependency, without doing anything it will start intercepting calls 
- will show a login screen, user: ```user```, password is printed in the console

## 6 Static Credentials ##
[common application properties](https://docs.spring.io/spring-boot/docs/current/reference/html/application-properties.html)
[yaml vs properties files](https://www.baeldung.com/spring-boot-yaml-vs-properties)

```
spring.security.user.name=myuser
spring.security.user.password=testing
```

## 7 Why Use Spring Security ##
- app security is very complex
- always new exploits, frameworks update and patch known vulnerabilities
- spring security is built by security experts, allowing us to secure our app for with very minimal configuration
- open source
- protects against common exploits
- Supports RBAC
- Supports various types of auth, JWT, OAuth2, OpenID, Username and Password etc...

## 8 Servlets VS Filters ##

![s vf](./images/serverlets-vs-filters.png)
- servlet is created internally by spring 
- filter is used to intercept every request to the application
- acts a middleware in between client and servlet/business logic

## 9 Spring Security Architecture ##

![spring security architecture](./images/spring-internal-flow.png)
1. user enters credentials
2. Filters create authentication object such as the ```UsernamePasswordAuthenticationToken```
3. Authentication object handed to the authentication manager
4. Authentication Manager, which is implemented by ```ProviderManager``` checks available authentication provider
5. Authentication Providers decide if user is valid
  - can be many authentication providers
  - can write logic in authentication providers to decide how to authenticate
  - Authentication manager will try all authentication providers, not just one
  - Can leverage spring security classes ```UserDetailsManager``` and ```UserDetailsService```
6. Encodes passwords as to not store in plain text
  - works with ```UserDetailsManager/Service``` to decide if the user should be authenticated
7. Sends response back to authentication manager
8. Forwards back to security filters
9. With an update Authentication Object, the filters store it in the security context
  - store authentication status, session id etc...
  - this is why user doesn't have to log in again
10. Final yes/no response sent back to end user


![security components](./images/spring_security_components.png)

## 11 Spring Security Sequence Flow ##

![sequence flow](./images/spring-security-sequence-flow.png)
![sequence flow](./images/spring-security-sequence-flow2.png)

## Annotations ##

```
@ComponentScan("com.eazybytes.sprinsecuritybasic.controller") 
```
- sprinsecuritybasic/src/main/java/com/eazybytes/sprinsecuritybasic/SprinsecuritybasicApplication.java

- optional in this setup as spring scans components in THIS and sub packages, however if we split our app out into a package outside springsecruitybasic then would need to annotate



# Section 2 #

## 17 Default Spring Security Configuration ##
- spring security protects all paths by default 
- this is configured in the ```defaultSecurityFilterChain``` bean


![default chain](./images/default-securit-filter-chain.png)

- ```http.authorizeRequests().anyRequest().authenticated();``` - says authorize any request made to the backend 

- from the comments 

```
The default configuration for web security. It relies on Spring Security's content-negotiation strategy to determine what sort of authentication to use. ** If the user specifies their own SecurityFilterChain bean, this will back-off completely and the users should specify all the bits that they want to configure as part of the custom security configuration **.
```
- if we create a bean of securityFilter chain then our custom auth will be taking place


## 18 Adding Custom Authentication Configuration ##
- can permit all or choose which paths need authentication

- see ./02_section/sprinsecuritysec2/src/main/java/com/eazybytes/config/ProjectSecurityConfig.java

## 19 Denying All Requests ##
- could deny all if they want
- could do this to either test security, go through updates or turn off the services for a period of time 

![deny-all](./images/deny-all.png)

```
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().anyRequest().denyAll();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }
```

## 20 Allowing All Requests ##

- could allow all requests
- could be done for development environments via conditional beans 

![allow-all](./images/permit-all.png)

```
    @Bean
    SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests().anyRequest().permitAll();
        http.formLogin();
        http.httpBasic();
        return http.build();
    }
```

# Section 3 #
## 22 Approach 1 Configuring users using inMemoryUserDetailsManager ##
- not for production
- can define multiple users along with their authorities with the help fo ```inMemoryUserDetailsManager``` and ```UserDetails```
- use the ```withDefaultPasswordEncoder()``` method, this method is deprecated

![approach1](./images/configuring-users-approach-1.png)



## 23 Approach 2 Configuring users using inMemoryUserDetailsManager ##
- not for production

![approach 2](./images/in-memory-user-approach-2.png)