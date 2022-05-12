package com.gf.swaggerdemo.controller;


import com.gf.swaggerdemo.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping(value = "hello")
    public String hello(){
        return "hello";
    }

    @PostMapping("/getUser")
    public User getUser(){
        return new User();
    }
}
