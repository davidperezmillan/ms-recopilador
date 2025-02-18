package com.davidperezmillan.recopilador.apllication.port;

import com.davidperezmillan.recopilador.domain.models.Pelicula;
import com.davidperezmillan.recopilador.domain.models.Serie;

import java.util.List;

public interface ScraperPort {

    List<Serie> scrapeAllSeries();

    List<Pelicula> scrapeAllMovies();
}
