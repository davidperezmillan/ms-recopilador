package com.davidperezmillan.recopilador.infrastructure.web.media.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class EnabledController {

    //Hello controller
    @GetMapping
    public String hello() {
        return "Hello World!";
    }

}

