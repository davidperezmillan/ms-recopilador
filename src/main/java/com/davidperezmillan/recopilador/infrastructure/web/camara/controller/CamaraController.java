package com.davidperezmillan.recopilador.infrastructure.web.camara.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/camara")
public class CamaraController {



    // curl 'http://192.168.68.127/cgi-bin/reboot.sh' \
    //  -H 'Accept: application/json, text/javascript, */*; q=0.01' \
    //  -H 'Accept-Language: es-ES,es;q=0.9' \
    //  -H 'Authorization: Basic ZGF2aWQ6Y2xvbjk4OTc=' \
    //  -H 'Connection: keep-alive' \
    //  -H 'Referer: http://192.168.68.127/index.html?page=maintenance' \
    //  -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36' \
    //  -H 'X-Requested-With: XMLHttpRequest' \
    //  --insecure


    @GetMapping("/reboot")
    public Mono<String> rebootCamera() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://192.168.68.127")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZGF2aWQ6Y2xvbjk4OTc=")
                .defaultHeader(HttpHeaders.CONNECTION, "keep-alive")
                .defaultHeader(HttpHeaders.REFERER, "http://192.168.68.127/index.html?page=maintenance")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                .defaultHeader("X-Requested-With", "XMLHttpRequest")
                .build();

        return webClient.get()
                .uri("/cgi-bin/reboot.sh")
                .retrieve()
                .bodyToMono(String.class);
    }

    @GetMapping("/reset")
    public Mono<String> resetCamera() {
        WebClient webClient = WebClient.builder()
                .baseUrl("http://192.168.68.127")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9")
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZGF2aWQ6Y2xvbjk4OTc=")
                .defaultHeader(HttpHeaders.CONNECTION, "keep-alive")
                .defaultHeader(HttpHeaders.REFERER, "http://192.168.68.127/index.html?page=maintenance")
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                .defaultHeader("X-Requested-With", "XMLHttpRequest")
                .build();

        return webClient.get()
                .uri("/cgi-bin/reset.sh")
                .retrieve()
                .bodyToMono(String.class);
    }

}
