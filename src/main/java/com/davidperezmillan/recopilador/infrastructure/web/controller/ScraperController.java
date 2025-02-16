package com.davidperezmillan.recopilador.infrastructure.web.controller;

import com.davidperezmillan.recopilador.apllication.service.ScraperLauncherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/scrapers")
public class ScraperController {

    private final ScraperLauncherService scraperLauncherService;

    public ScraperController(ScraperLauncherService scraperLauncherService) {
        this.scraperLauncherService = scraperLauncherService;
    }

    @GetMapping("/run-all")
    public CompletableFuture<List<String>> runAllScrapers() {
        return scraperLauncherService.launchAllScrapers();
    }
}

