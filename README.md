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

### Adding Lombok ###
- for slf4j logging annotation 

```
    <dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<optional>true</optional>
		</dependency>
```

and exclude from the build 


```
  build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
				<configuration>
					<excludes>
						<exclude>
							<groupId>org.projectlombok</groupId>
							<artifactId>lombok</artifactId>
						</exclude>
					</excludes>
				</configuration>
			</plugin>
		</plugins>
  </build>

```

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
- here we create a bean of password encoder

![approach 2](./images/in-memory-user-approach-2.png)

## 24 Understanding User Management Interfaces and Classes ## 

![user management](./images/user-management-classes-and-interface.png)

![spring security architecture](./images/spring-internal-flow.png)

- the ```DaoAuthenticationProvider``` that comes with spring will look to user the ```inMemoryUserDetailsManager```, ```JdbcUserDetailsManager``` and ```LdapUserDetailsManager``` 
- the details managers are sample implementations provided by spring security
- The ```UserDetails``` interface allows us to represent the User as a ```User``` class that implements the ```UserDetails```
- When you return a new class of type ```...UserDetailsManager```, the ```loadUserByUsername``` method is called
  - this method returns ```UserDetails``` which implements ```UserDetailsManager ``` which extends ```UserDetailsService```

## 25 Deep Dive of UserDetails Interface and User Class ##
- holds methods for 
  1. getAuthorities()
  2. getPassword()
  3. getUsername()
  4. isAccountNonExpired()
  5. isAccountNonLocked()
  6. isCredentialsNonExpired()
  7. isEnabled()

- sample of this interface are the ```User``` Class, we can use this or implement our own ```userDetails```

- this object is readOnly, there are not setters, once the object is created through the constructor it is immutable
- 

![auth vs user details](./images/authentication-vs-userdetails.png)
- identify if authentication is successful or not inside the authentication providers
- ```AuthenticationProvider``` will convert the userDetails into the Authentication Token, after fetched from the database and authenticated
- this is done by default in the ```AbstractUserDetailsAuthenticationProvider.java```,  ```authenticate``` method, if authenticated then it called the ```createSuccessAuthentication``` method witch takes in an auth token, user and returns back a populated authentication token

## 26 Deep Dive of UserDetailsService and UserDetailsManager ##
- ```UserDetailsService```, holds the method ```loadUserByUsername``` which loads the user from the database
- only username is loaded, not the password, which we dont want to move over the network
- ```UserDetailsService```is extended by the ```UserDetailsManager``` which gives the ability to perform CRUD operations on users
   - offers a userExists() method that tells if that user already exists in the system

## 27 Deep Dive of UserDetailsManager implementation classes ##
- ```inMemoryUserDetailsManager```, ```JdbcUserDetailsManager``` and ```LdapUserDetailsManager```  are the most commonly used and examples are provided by spring


### inMemoryUserDetailsManager ###

- for ```inMemoryUserDetailsManager``` the ```createUser()``` method is called through the constructor 
- holds the ```loadUserByUsername()``` method 
- ```DaoAuthenticationProvider``` knows which details manager to invoke by which beans are created 
- ```inMemoryUserDetailsManager``` used mostly for dev and demo

### JdbcUserDetailsManager ###
- most common for production 
- spring security assumes a specific table structure, they have programmed queries for this configuration 
- table name must be ```users```
- there is a default schema included in the class, pointing to a ddl file. 
- can use the ```users.ddl``` file to create the database architecture


### Group Manager ###
- interface
- implemented by ```JdbcUserDetailsManager```
- helps create and add users to groups for RBAC


### LdapUserDetailsManager ###
- not as common
- still has ```loadUserByUserName```
- have to have an LDAP server configured

### Adding LDAP ###
- add following dependencies 

```
    <dependency>
			<groupId>org.springframework.ldap</groupId>
			<artifactId>spring-ldap-core</artifactId>
		</dependency>
		<dependency>
			<groupId>org.springframework.security</groupId>
			<artifactId>spring-security-ldap</artifactId>
		</dependency>
```

## 28 Creating MySQL DB ##


- can just do this in docker 

``` docker pull mysql```

```docker run --name spring-security-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=pwd -e useSSL=false -d mysql```

### Connecting with DBeaver ###
- host = localhost
- username = root
- pwd = pwd
- port = 3306 
- go to driver properties -> set ```allowPublicKeyRetrieval``` to true
- https://stackoverflow.com/questions/61749304/connection-between-dbeaver-mysql

## 29 Connecting to DB ##

- have to create and seed the database

```
create database eazybank;

use eazybank;

CREATE TABLE `users` (
`id` INT NOT NULL AUTO_INCREMENT,
`username` VARCHAR(45) NOT NULL,
`password` VARCHAR(45) NOT NULL,
`enabled` INT NOT NULL,
PRIMARY KEY (`id`));

CREATE TABLE `authorities` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `authority` varchar(45) NOT NULL,
  PRIMARY KEY (`id`));

INSERT IGNORE INTO `users` VALUES (NULL, 'happy', '12345', '1');
INSERT IGNORE INTO `authorities` VALUES (NULL, 'happy', 'write');

CREATE TABLE `customer` (
  `id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(45) NOT NULL,
  `pwd` varchar(200) NOT NULL,
  `role` varchar(45) NOT NULL,
  PRIMARY KEY (`id`)
);

INSERT INTO `customer` (`email`, `pwd`, `role`)
 VALUES ('t@t.com', '54321', 'admin');

```

- can be used for small or flexible projects but example tables might not be usable for everyone

## 30 Using JdbcUserDetailsManager ##

- have to add dependencies for spring jdbc, mysql and spring data jpa

```
    <dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-jdbc</artifactId>
		</dependency>
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-data-jpa</artifactId>
		</dependency>
```


- connect with 

```
# dont inject like this in production, should come from devops team
spring.datasource.url=jdbc:mysql://localhost/eazybank
spring.datasource.username=root
spring.datasource.password=pwd


#print sql in the console, not for production
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

- will then create the JdbcUserDetailsManager

```
    @Bean
    public UserDetailsService userDetailsService(DataSource dataSource){
        return new JdbcUserDetailsManager(dataSource);
    }

```

- Spring boot creates an object of type data source in memory when we add jbdc information to class path and application properties

- not sure if you can have multiple UserDetailsManagers


## 31 Creating Custom Authentication Tables ##
- in schema above
- will have to create your own JPA entity, wont be able to sue the default one

## 32 Creating JPA Entity and Repo for new table ##
- have to create a repository
```
sprinsecuritysec3/src/main/java/com/eazybytes/repository/CustomerRepository.java
```
- have to create a model
```
sprinsecuritysec3/src/main/java/com/eazybytes/model/Customer.java
```

### Annotations ###
- have to add 2 annotations to spring application if repositories or entities are not in the main package
- will scan and create beans for the packages

```
@EnableJpaRepositories("com.eazybytes.repository")
@EntityScan("com.eazybytes.model")
```

- if not using the spring security starter must have annotation to turn on web security

```
@EnableWebSecurity
```

## 33 Create custom implementation of userDetailsService ##
- if we are using our own database setup, we must override the default ```UserDetailsService``` and write logic for loading the user in the ```loadUserByUsername``` method

- sprinsecuritysec3/src/main/java/com/eazybytes/config/EazyBankUserDetails.java

### multiple userDetailsService ###
- if you have ```userDetailsService``` it will confuse the ```DaoAuthenticationProvider```

## 34 Allowing new user registration ##
- Could override the ```UserDetailsManager``` ```createUser() ``` method 
- see for example sprinsecuritysec3/src/main/java/com/eazybytes/controller/LoginController.java
- have to permit non authenticated users to hit the /register path
- this point can create with postman
```
{
    "email": "1@1.com",
    "pwd": "12345",
    "role": "user"
}
```

- successful response will be ```Given user details are successfully registered```

## Update Sequence with custom  JBDC User Details Service ##

![s1](./images/custom-sequence-1.png)
![s2](./images/custom-sequence-2.png)


# Section 4 #
