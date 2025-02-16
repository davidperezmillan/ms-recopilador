package com.davidperezmillan.recopilador.infrastructure.web.controller;

import com.davidperezmillan.recopilador.apllication.usecases.ScraperUseCase;
import com.davidperezmillan.recopilador.domain.models.Serie;
import com.davidperezmillan.recopilador.infrastructure.web.controller.dtos.ApplicationResponse;
import com.davidperezmillan.recopilador.infrastructure.web.controller.dtos.SerieResponse;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/scrapers")
public class ScraperController {

    private final ScraperUseCase scraperUseCase;

    public ScraperController(ScraperUseCase scraperUseCase) {
        this.scraperUseCase = scraperUseCase;
    }

    @GetMapping("/all")
    public CompletableFuture<ApplicationResponse<List<SerieResponse>>> runAllScrapers() {
        CompletableFuture<List<Serie>> series = scraperUseCase.scrapAllSeries();
        ApplicationResponse<List<SerieResponse>> respuesta =
                new ApplicationResponse<List<SerieResponse>>(series.join().size(), SerieResponseMapper.map(series.join()));
        return CompletableFuture.completedFuture(respuesta);
    }
}

