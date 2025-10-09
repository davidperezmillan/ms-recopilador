package com.davidperezmillan.recopilador.infrastructure.health.services;

import com.davidperezmillan.recopilador.domain.utils.CamarasUtils;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class HealthWebService {


    public EventsResponse getEventsDir(String camara) {
        try{
            String ipCamara = CamarasUtils.getUrlCamara(camara);
            WebClient webClient = WebClient.builder()
                    .baseUrl(ipCamara)
                    .defaultHeader(HttpHeaders.ACCEPT, "application/json, text/javascript, */*; q=0.01")
                    .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZGF2aWQ6Y2xvbjk4OTc=")
                    .defaultHeader(HttpHeaders.CONNECTION, "keep-alive")
                    .defaultHeader(HttpHeaders.REFERER, ipCamara + "/?page=eventsdir")
                    .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/139.0.0.0 Safari/537.36")
                    .defaultHeader("X-Requested-With", "XMLHttpRequest")
                    .build();

            return webClient.get()
                    .uri("/cgi-bin/eventsdir.sh")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(EventsResponse.class)
                    .map(eventsResponse -> {
                        eventsResponse.getRecords().sort((a, b) -> b.getDatetime().compareTo(a.getDatetime()));
                        return eventsResponse;
                    }).block();
        } catch (IllegalArgumentException e) {
            return null;

        }

    }
}
