package com.davidperezmillan.recopilador.apllication.usecases;

import com.davidperezmillan.recopilador.domain.models.Serie;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ScraperUseCase {

    CompletableFuture<List<Serie>> scrapAllSeries();
}
