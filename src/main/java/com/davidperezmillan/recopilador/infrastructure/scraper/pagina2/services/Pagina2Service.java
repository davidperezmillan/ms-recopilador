package com.davidperezmillan.recopilador.infrastructure.scraper.pagina2.services;

import com.davidperezmillan.recopilador.apllication.port.ScraperPort;
import com.davidperezmillan.recopilador.domain.models.Pelicula;
import com.davidperezmillan.recopilador.domain.models.Serie;
import com.davidperezmillan.recopilador.infrastructure.scraper.mappers.ScrapSerieMapper;
import com.davidperezmillan.recopilador.infrastructure.scraper.models.ScrapSerie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Pagina2Service implements ScraperPort {

    @Override
    public List<Serie> scrapeAllSeries() {
        ScrapSerie scrapSerie = new ScrapSerie();
        scrapSerie.setTitle("Serie 2");

        return List.of(ScrapSerieMapper.map(scrapSerie));
        // Lógica de scraping para la página 2
    }

    @Override
    public List<Pelicula> scrapeAllMovies() {
        return List.of();
    }
}
