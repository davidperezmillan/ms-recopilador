package com.davidperezmillan.recopilador.infrastructure.transmission.services;

import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.StatusTorrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.request.ArgumentsRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.request.TransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionResponse;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Service
@Log4j2
public class TransmissionServerRestService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Setter
    private Transmission transmission;

    private String sessionId;

    public List<TransmissionTorrent> listTorrent() {
        if (sessionId == null) {
            HttpHeaders headers = createHeaders();
            getResponseHeaders(headers);
        }
        try {
            log.info("Session ID: {}", sessionId);
            HttpHeaders headers = createHeaders();
            headers.set("X-Transmission-Session-Id", sessionId);
            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-get");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFields(new String[]{"id", "name", "status", "percentDone", "hashString", "files"});
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmission.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            if (null != respuesta.getBody().getArguments().getTorrents()) {
                return List.of(respuesta.getBody().getArguments().getTorrents());
            }
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return listTorrent();
            }
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
        return List.of();
    }

    public TransmissionTorrent getTorrentByHashString(String hashString) {
        if (sessionId == null) {
            HttpHeaders headers = createHeaders();
            getResponseHeaders(headers);
        }
        try {
            log.info("Session ID: {}", sessionId);
            HttpHeaders headers = createHeaders();
            headers.set("X-Transmission-Session-Id", sessionId);
            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-get");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFields(new String[]{"id", "name", "status", "percentDone", "hashString", "files"});
            arguments.setIds(new String[]{hashString});
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<TransmissionResponse> response = restTemplate.exchange(transmission.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            return response.getBody().getArguments().getTorrents()[0];
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return getTorrentByHashString(hashString);
            }
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
        return null;
    }

    public TransmissionTorrent addTorrent(Torrent torrent) {
        if (sessionId == null) {
            HttpHeaders headers = createHeaders();
            getResponseHeaders(headers);
        }
        try {
            HttpHeaders headers = createHeaders();
            headers.set("X-Transmission-Session-Id", sessionId);
            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-add");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFilename(torrent.getUrl());
            if (torrent.getDownloadPath() != null) {
                arguments.setDownloadDir(torrent.getDownloadPath());
            }
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmission.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            if (respuesta.getBody().getResult().equals("success")) {
                TransmissionTorrent transmissionTorrent = new TransmissionTorrent();
                if (null != respuesta.getBody().getArguments().getTorrentDuplicate()) {
                    log.info("Torrent duplicado: {}", respuesta.getBody().getArguments().getTorrentDuplicate());
                    transmissionTorrent = respuesta.getBody().getArguments().getTorrentDuplicate();
                } else {
                    log.info("Torrent añadido: {}", respuesta.getBody());
                    transmissionTorrent = respuesta.getBody().getArguments().getTorrentAdded();
                }

                torrent.setStatus(StatusTorrent.DOWNLOADING);
                torrent.setTitle(transmissionTorrent.getName());
                torrent.setIdTransmission(transmissionTorrent.getId());
                torrent.setHashString(transmissionTorrent.getHashString());
                torrent.setPercentDone(transmissionTorrent.getPercentDone());

                return transmissionTorrent;
            }
            log.warn("Error al añadir el torrent: {}", respuesta.getBody().getResult());
            torrent.setStatus(StatusTorrent.PENDING);
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return addTorrent(torrent);
            }
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
        return null;
    }

    private void getResponseHeaders(HttpHeaders headers) {
        try {
            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(new TransmissionRequest(), headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmission.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);
            sessionId = respuesta.getHeaders().getFirst("X-Transmission-Session-Id");
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
    }

    private HttpHeaders createHeaders() {
        String auth = transmission.getUsername() + ":" + transmission.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }
}