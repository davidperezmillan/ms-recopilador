package com.davidperezmillan.recopilador.infrastructure.scraper.pagina2.services;

import com.davidperezmillan.recopilador.apllication.port.ScraperPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Pagina2Service implements ScraperPort {
    @Override
    public List<String> scrape() {
        return List.of("Scrapeando página 2...");
        // Lógica de scraping para la página 2
    }
}
