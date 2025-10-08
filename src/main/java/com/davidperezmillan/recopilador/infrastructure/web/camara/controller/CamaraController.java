package com.davidperezmillan.recopilador.infrastructure.web.camara.controller;

import com.davidperezmillan.recopilador.apllication.usecases.CameraHealthUseCase;
import com.davidperezmillan.recopilador.domain.models.Camaras;
import com.davidperezmillan.recopilador.infrastructure.health.HealthStatus;
import com.davidperezmillan.recopilador.infrastructure.web.camara.dtos.EventsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Tag(name = "Cámaras", description = "Operaciones sobre cámaras IP")
@RestController
@RequestMapping("/camara")
public class CamaraController {



    private final CameraHealthUseCase cameraHealthUseCase;

    public CamaraController(CameraHealthUseCase cameraHealthUseCase) {
        this.cameraHealthUseCase = cameraHealthUseCase;
    }


    // curl 'http://192.168.68.127/cgi-bin/reboot.sh' \
    //  -H 'Accept: application/json, text/javascript, */*; q=0.01' \
    //  -H 'Accept-Language: es-ES,es;q=0.9' \
    //  -H 'Authorization: Basic ZGF2aWQ6Y2xvbjk4OTc=' \
    //  -H 'Connection: keep-alive' \
    //  -H 'Referer: http://192.168.68.127/index.html?page=maintenance' \
    //  -H 'User-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36' \
    //  -H 'X-Requested-With: XMLHttpRequest' \
    //  --insecure


    /**
     * Reinicia todas las cámaras IP configuradas y devuelve la respuesta combinada.
     * @return Respuesta combinada de todas las cámaras.
     */
    @Operation(summary = "Reiniciar cámaras", description = "Reinicia todas las cámaras IP configuradas y devuelve la respuesta combinada.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta combinada de reinicio",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "Cámara salon: OK\nCámara hab: OK")))
    })
    @GetMapping("/reboot")
    public Mono<String> rebootCamera() {
        List<Mono<String>> peticiones = new ArrayList<>();
        for (Camaras camara : Camaras.values()) {
            String ipCamara = camara.getUrl();
            WebClient webClient = WebClient.builder()
                    .baseUrl(ipCamara)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZGF2aWQ6Y2xvbjk4OTc=")
                    .defaultHeader(HttpHeaders.CONNECTION, "keep-alive")
                    .defaultHeader(HttpHeaders.REFERER, "http://192.168.68.127/index.html?page=maintenance")
                    .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                    .defaultHeader("X-Requested-With", "XMLHttpRequest")
                    .build();

            peticiones.add(
                    webClient.get()
                            .uri("/cgi-bin/reboot.sh")
                            .retrieve()
                            .bodyToMono(String.class)
                            .map(respuesta -> camara + ": " + respuesta)
            );
        }
        return Mono.zip(peticiones, resultados -> {
            StringBuilder combinado = new StringBuilder();
            for (Object resultado : resultados) {
                combinado.append(resultado.toString()).append("\n");
            }
            return combinado.toString();
        });
    }


    @Operation(summary = "Reiniciar cámara específica", description = "Reinicia una cámara IP específica.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta de reinicio",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "OK")))
    })
    @GetMapping("/reboot/{camara}")
    public Mono<String> rebootCameraByCamara(
        @Parameter(description = "Nombre de la cámara", example = "salon")
        @PathVariable("camara") String camara) {
        try{
            String ipCamara = getUrlCamara(camara);
            WebClient webClient = WebClient.builder()
                    .baseUrl(ipCamara)
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
        } catch (IllegalArgumentException e) {
           return Mono.error(new IllegalArgumentException("Cámara no válida" + camara) );

        }


    }



    @Operation(summary = "Obtener eventos de cámara", description = "Obtiene el directorio de eventos de una cámara IP.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de eventos",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"records\":[{\"datetime\":\"2025-10-07T10:00:00\",\"event\":\"Movimiento detectado\"}]}")))
    })
    @GetMapping("/eventsdir/{camara}")
    public Mono<EventsResponse> getEventsDir(
        @Parameter(description = "Nombre de la cámara", example = "salon")
        @PathVariable String camara) {
        try{
            String ipCamara = getUrlCamara(camara);
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
                    });
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Cámara no válida" + camara) );

        }

    }

    @Operation(summary = "Obtener snapshot de cámara", description = "Obtiene una imagen snapshot de una cámara IP.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Imagen JPEG",
            content = @Content(mediaType = "image/jpeg",
                examples = @ExampleObject(value = "<bytes de imagen>") ))
    })
    @GetMapping("snapshot/{camara}")
    public Mono<byte[]> getSnapshot(
        @Parameter(description = "Nombre de la cámara", example = "salon")
        @PathVariable String camara) {
        try{
            String ipCamara = getUrlCamara(camara);
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
                    .uri("/cgi-bin/snapshot.sh")
                    .accept(MediaType.IMAGE_JPEG)
                    .retrieve()
                    .bodyToMono(byte[].class);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Cámara no válida :" + camara) );

        }

    }

    @Operation(summary = "Resetear todas las camaras", description = "Resetea la cámara principal.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta de reseteo",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "OK")))
    })
    @GetMapping("/reset")
    public Mono<String> resetCamera() {
        List<Mono<String>> peticiones = new ArrayList<>();
        for (Camaras camara : Camaras.values()) {
            String ipCamara = camara.getUrl();
            WebClient webClient = WebClient.builder()
                    .baseUrl(ipCamara)
                    .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                    .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "es-ES,es;q=0.9")
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic ZGF2aWQ6Y2xvbjk4OTc=")
                    .defaultHeader(HttpHeaders.CONNECTION, "keep-alive")
                    .defaultHeader(HttpHeaders.REFERER, "http://192.168.68.127/index.html?page=maintenance")
                    .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36")
                    .defaultHeader("X-Requested-With", "XMLHttpRequest")
                    .build();

            peticiones.add(
                webClient.get()
                        .uri("/cgi-bin/reset.sh")
                        .retrieve()
                        .bodyToMono(String.class)
            );
        }
        return Mono.zip(peticiones, resultados -> {
            StringBuilder combinado = new StringBuilder();
            for (Object resultado : resultados) {
                combinado.append(resultado.toString()).append("\n");
            }
            return combinado.toString();
        });
    }

    @Operation(summary = "Resetear cámara específica", description = "Resetea una cámara IP específica.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Respuesta de reseteo",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "OK")))
    })
    @GetMapping("/reset/{camara}")
    public Mono<String> resetCameraByCamara(
        @Parameter(description = "Nombre de la cámara", example = "salon")
        @PathVariable("camara") String camara) {
        try{
            String url = getUrlCamara(camara);
            WebClient webClient = WebClient.builder()
                    .baseUrl(url)
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
        } catch (IllegalArgumentException e) {
              return Mono.error(new IllegalArgumentException("Cámara no válida" + camara) );
        }
    }

    @Operation(summary = "Verificar salud de todas las cámaras", description = "Comprueba el estado de salud de todas las cámaras IP configuradas.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado de salud de todas las cámaras",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"salon\":{\"healthy\":true,\"details\":\"HTTP status: 200\"},\"habitacion\":{\"healthy\":false,\"details\":\"Error: Connection timeout\"}}")))
    })
    @GetMapping("/health")
    public Mono<HashMap<Camaras, HealthStatus>> checkAllCamerasHealth() {
        return Mono.just(cameraHealthUseCase.checkAllCamerasHealthAsync());
    }

    @Operation(summary = "Verificar salud de cámara específica", description = "Comprueba el estado de salud de una cámara IP específica.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estado de salud de la cámara",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"healthy\":true,\"details\":\"HTTP status: 200\"}"))),
        @ApiResponse(responseCode = "400", description = "Cámara no válida",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "Cámara no válida: test")))
    })
    @GetMapping("/health/{camara}")
    public Mono<HealthStatus> checkCameraHealth(
        @Parameter(description = "Nombre de la cámara", example = "salon")
        @PathVariable String camara) {
        try {
            HealthStatus status = cameraHealthUseCase.checkCameraHealth(camara);
            return Mono.just(status);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalArgumentException("Cámara no válida: " + camara));
        }
    }

    private String getUrlCamara(String camaraValue) throws IllegalArgumentException{
        for (Camaras camara : Camaras.values()) {
            if (camara.getNombre().equalsIgnoreCase(camaraValue)) {
                return camara.getUrl();
            }
        }
        Camaras camara = Camaras.valueOf(camaraValue.toUpperCase());
        return camara.getUrl();
    }
}
