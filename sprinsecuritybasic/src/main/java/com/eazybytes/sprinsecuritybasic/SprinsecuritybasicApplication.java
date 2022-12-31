package com.eazybytes.sprinsecuritybasic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.eazybytes.sprinsecuritybasic.controller") // optional in this setup as spring scans components in THIS and sub packages, however if we split our app out into a package outside springsecruitybasic then would need to annotate
public class SprinsecuritybasicApplication {

	public static void main(String[] args) {
		SpringApplication.run(SprinsecuritybasicApplication.class, args);
	}

}
