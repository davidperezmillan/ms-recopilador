package com.davidperezmillan.recopilador.infrastructure.web.controller;

import com.davidperezmillan.recopilador.apllication.service.ScraperLauncherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping()
public class EnabledController {

    //Hello controller
    @GetMapping
    public String hello() {
        return "Hello World!";
    }

}

