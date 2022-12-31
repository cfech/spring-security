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


## Annotations ##

```
@ComponentScan("com.eazybytes.sprinsecuritybasic.controller") 
```
- sprinsecuritybasic/src/main/java/com/eazybytes/sprinsecuritybasic/SprinsecuritybasicApplication.java

- optional in this setup as spring scans components in THIS and sub packages, however if we split our app out into a package outside springsecruitybasic then would need to annotate

