package com.davidperezmillan.recopilador.infrastructure.scraper.pagina3.services;

import com.davidperezmillan.recopilador.apllication.port.ScraperPort;
import com.davidperezmillan.recopilador.domain.models.Serie;
import com.davidperezmillan.recopilador.infrastructure.scraper.mappers.ScrapSerieMapper;
import com.davidperezmillan.recopilador.infrastructure.scraper.models.ScrapSerie;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Pagina3Service implements ScraperPort {

    @Override
    public List<Serie> scrapeAllSeries() {
        ScrapSerie scrapSerie = new ScrapSerie();
        scrapSerie.setTitle("Serie 3");

        return List.of(ScrapSerieMapper.map(scrapSerie));
        // Lógica de scraping para la página 1
    }
}
