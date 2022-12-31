package com.eazybytes.sprinsecuritybasic.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @GetMapping("/hello")
    public String sayHello(){
        return " Welcome to spring with security";
    }

}
