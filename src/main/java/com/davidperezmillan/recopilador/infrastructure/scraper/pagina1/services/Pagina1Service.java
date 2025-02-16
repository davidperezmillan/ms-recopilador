package com.davidperezmillan.recopilador.infrastructure.scraper.pagina1.services;

import com.davidperezmillan.recopilador.apllication.port.ScraperPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Pagina1Service implements ScraperPort {

    @Override
    public List<String> scrape() {
        return List.of("Scrapeando página 1...");
        // Lógica de scraping para la página 1
    }
}
