package com.davidperezmillan.recopilador.infrastructure.web.media.controller;

import com.davidperezmillan.recopilador.apllication.usecases.ScraperUseCase;
import com.davidperezmillan.recopilador.domain.models.Serie;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import com.davidperezmillan.recopilador.infrastructure.web.media.dtos.SerieResponse;
import com.davidperezmillan.recopilador.infrastructure.web.media.mappers.SerieResponseMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Tag(name = "Scrapers", description = "Operaciones para ejecutar scrapers de series")
@RestController
@RequestMapping("/scrapers")
public class ScraperController {

    private final ScraperUseCase scraperUseCase;

    public ScraperController(ScraperUseCase scraperUseCase) {
        this.scraperUseCase = scraperUseCase;
    }

    /**
     * Ejecuta todos los scrapers de series y devuelve la lista de resultados.
     */
    @Operation(summary = "Ejecutar todos los scrapers", description = "Ejecuta todos los scrapers de series y devuelve la lista de resultados.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de series scrapeadas",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":[{\"nombre\":\"Serie1\",\"temporadas\":2}]}")))
    })
    @GetMapping("/all")
    public CompletableFuture<ApplicationResponse<List<SerieResponse>>> runAllScrapers() {
        CompletableFuture<List<Serie>> series = scraperUseCase.scrapAllSeries();
        ApplicationResponse<List<SerieResponse>> respuesta =
                new ApplicationResponse<List<SerieResponse>>(series.join().size(), SerieResponseMapper.map(series.join()));
        return CompletableFuture.completedFuture(respuesta);
    }
}
