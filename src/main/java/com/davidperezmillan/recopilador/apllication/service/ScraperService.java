package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.port.ScraperPort;
import com.davidperezmillan.recopilador.apllication.usecases.ScraperUseCase;
import com.davidperezmillan.recopilador.domain.models.Serie;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@EnableAsync
public class ScraperService implements ScraperUseCase {

    private final List<ScraperPort> scrapers;

    public ScraperService(List<ScraperPort> scrapers) {
        this.scrapers = scrapers;
    }


    @Override
    @Async
    public CompletableFuture<List<Serie>> scrapAllSeries() {
        List<CompletableFuture<List<Serie>>> futures = scrapers.stream()
                .map(scraper -> CompletableFuture.supplyAsync(scraper::scrapeAllSeries))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join) // Espera a que terminen y obtiene los resultados
                        .flatMap(List::stream) // Une todas las listas en una sola
                        .collect(Collectors.toList()));
    }

}
