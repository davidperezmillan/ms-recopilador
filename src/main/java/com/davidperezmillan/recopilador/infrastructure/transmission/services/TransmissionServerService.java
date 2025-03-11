package com.davidperezmillan.recopilador.infrastructure.transmission.services;

import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AddTransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AllTorrentRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.ServerTransmission;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.request.ArgumentsRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.request.TransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionResponse;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@Log4j2
public class TransmissionServerService {

    private final WebClient webClient = WebClient.builder().build();
    private String sessionId;




    public TransmissionTorrent addTorrent(AddTransmissionRequest addTransmissionRequest) {
        if (sessionId == null) {
            getSessionId(addTransmissionRequest.getServer());
        }
        try {
            HttpHeaders headers = createHeaders(addTransmissionRequest.getServer());
            headers.set("X-Transmission-Session-Id", sessionId);

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-add");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFilename(addTransmissionRequest.getMangetLink());
            if (addTransmissionRequest.getDownloadPath() != null) {
                arguments.setDownloadDir(addTransmissionRequest.getDownloadPath());
            }
            request.setArguments(arguments);

            TransmissionResponse response = webClient.post()
                    .uri(addTransmissionRequest.getServer().getUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TransmissionResponse.class)
                    .block();


            if (response != null && "success".equals(response.getResult())) {
                TransmissionTorrent transmissionTorrent = response.getArguments().getTorrentAdded();
                if (response.getArguments().getTorrentDuplicate() != null) {
                    log.info("Torrent duplicado: {}", response.getArguments().getTorrentDuplicate());
                    transmissionTorrent = response.getArguments().getTorrentDuplicate();
                } else {
                    log.info("Torrent añadido: {}", response);
                }
                return transmissionTorrent;
            }
            log.warn("Error al añadir el torrent: {}", response != null ? response.getResult() : "Unknown error");
        } catch (WebClientResponseException.Conflict e) {
            sessionId = e.getHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return addTorrent(addTransmissionRequest);
            }
        } catch (Exception e) {
            log.error("Transmission no está disponible.", e);
        }
        return null;
    }

    public List<String> getListDownloadDir(AllTorrentRequest allTorrentRequest){
        List<TransmissionTorrent> allTorrents = getAllTorrents(allTorrentRequest);
        return allTorrents.stream().map(TransmissionTorrent::getDownloadDir).distinct().toList();
    }

    public List<TransmissionTorrent> getAllTorrents(AllTorrentRequest allTorrentRequest) {
        if (sessionId == null) {
            getSessionId(allTorrentRequest.getServer());
        }
        try {
            HttpHeaders headers = createHeaders(allTorrentRequest.getServer());
            headers.set("X-Transmission-Session-Id", sessionId);

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-get");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFields(new String[]{"id", "name", "status", "percentDone", "downloadDir", "hashString", "files"});
            request.setArguments(arguments);

            TransmissionResponse response = webClient.post()
                    .uri(allTorrentRequest.getServer().getUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TransmissionResponse.class)
                    .block();

            return response != null ? List.of(response.getArguments().getTorrents()) : null;
        } catch (WebClientResponseException.Conflict e) {
            sessionId = e.getHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return getAllTorrents(allTorrentRequest);
            }
        } catch (Exception e) {
            log.error("Transmission no está disponible.", e);
        }
        return List.of();
    }

    public TransmissionResponse setAltSpeedEnabled(ServerTransmission server, boolean active) {
        if (sessionId == null) {
            getSessionId(server);
        }
        try {
            HttpHeaders headers = createHeaders(server);
            headers.set("X-Transmission-Session-Id", sessionId);

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("session-set");

            ArgumentsRequest arguments = new ArgumentsRequest();
            // Asumiendo que tienes estos setters en ArgumentsRequest.
            arguments.setAltSpeedEnabled(active);
            request.setArguments(arguments);

            TransmissionResponse response = webClient.post()
                    .uri(server.getUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(TransmissionResponse.class)
                    .block();

            if (response != null && "success".equals(response.getResult())) {
                log.info("Límites de velocidad globales {}.", active ? "activados" : "desactivados");
            } else {
                log.warn("Error al modificar los límites globales: {}",
                        response != null ? response.getResult() : "Unknown error");
            }
            return response;
        } catch (WebClientResponseException.Conflict e) {
            sessionId = e.getHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID actualizado por error: {}", sessionId);
            if (sessionId != null) {
                return setAltSpeedEnabled(server, active);
            }
        } catch (Exception e) {
            log.error("Transmission no está disponible.", e);
        }
        return null;
    }




    private void getSessionId(ServerTransmission serverTransmission) {
        try {
            HttpHeaders headers = createHeaders(serverTransmission);
            TransmissionResponse response = webClient.post()
                    .uri(serverTransmission.getUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .retrieve()
                    .bodyToMono(TransmissionResponse.class)
                    .block();
            sessionId = response != null ? response.getResult() : null;
        } catch (WebClientResponseException.Conflict e) {
            sessionId = e.getHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
        } catch (Exception e) {
            log.error("Transmission no está disponible.", e);
        }
    }




    private HttpHeaders createHeaders(ServerTransmission serverTransmission) {
        String auth = serverTransmission.getUsername() + ":" + serverTransmission.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }
}
