package com.davidperezmillan.recopilador.infrastructure.transmission.services;


import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.StatusTorrent;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionServerRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionServiceRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionServiceRespone;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.request.ArgumentsRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.request.TransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionResponse;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
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
public class TransmissionServerService {

    private final RestTemplate restTemplate = new RestTemplate();

    private String sessionId;

    public List<TransmissionServiceRespone> listTorrent(TransmissionServiceRequest transmissionServiceRequest) {
        if (sessionId == null) {
            HttpHeaders headers = createHeaders(transmissionServiceRequest.getTransmissionServerRequest());
            getResponseHeaders(transmissionServiceRequest.getTransmissionServerRequest(), headers);
        }
        try {
            log.info("Session ID: {}", sessionId);
            HttpHeaders headers = createHeaders(transmissionServiceRequest.getTransmissionServerRequest());
            headers.set("X-Transmission-Session-Id", sessionId);
            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-get");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFields(new String[]{"id", "name", "status", "percentDone", "hashString", "files"});
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmissionServiceRequest.getTransmissionServerRequest().getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            if (null != respuesta.getBody().getArguments().getTorrents()) {
                return List.of(respuesta.getBody().getArguments().getTorrents());
            }
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return listTorrent(transmissionServiceRequest);
            }
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
        return List.of();
    }

    public TransmissionServiceRespone getTorrentByHashString(TransmissionServiceRequest transmissionServiceRequest) {
        if (sessionId == null) {
            HttpHeaders headers = createHeaders(transmissionServiceRequest.getTransmissionServerRequest());
            getResponseHeaders(transmissionServiceRequest.getTransmissionServerRequest(), headers);
        }
        try {
            log.info("Session ID: {}", sessionId);
            HttpHeaders headers = createHeaders(transmissionServiceRequest.getTransmissionServerRequest());
            headers.set("X-Transmission-Session-Id", sessionId);
            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-get");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFields(new String[]{"id", "name", "status", "percentDone", "hashString", "files"});
            arguments.setIds(new String[]{transmissionServiceRequest.getHashString()});
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<TransmissionResponse> response = restTemplate.exchange(
                    transmissionServiceRequest.getTransmissionServerRequest().getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            return response.getBody().getArguments().getTorrents()[0];
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return getTorrentByHashString(transmissionServiceRequest);
            }
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
        return null;
    }

    public TransmissionServiceRespone addTorrent(TransmissionServiceRequest transmissionServiceRequest) {
        if (sessionId == null) {
            HttpHeaders headers = createHeaders(transmissionServiceRequest.getTransmissionServerRequest());
            getResponseHeaders(transmissionServiceRequest.getTransmissionServerRequest(), headers);
        }
        try {
            HttpHeaders headers = createHeaders(transmissionServiceRequest.getTransmissionServerRequest());
            headers.set("X-Transmission-Session-Id", sessionId);
            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-add");
            ArgumentsRequest arguments = new ArgumentsRequest();
            arguments.setFilename(transmissionServiceRequest.getUrl());
            if (transmissionServiceRequest.getDownloadPath() != null) {
                arguments.setDownloadDir(transmissionServiceRequest.getDownloadPath());
            }
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmissionServiceRequest.getTransmissionServerRequest().getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            if (respuesta.getBody().getResult().equals("success")) {
                TransmissionTorrent transmissionTorrent = new TransmissionTorrent();
                if (null != respuesta.getBody().getArguments().getTorrentDuplicate()) {
                    log.info("Torrent duplicado: {}", respuesta.getBody().getArguments().getTorrentDuplicate());
                    transmissionTorrent = respuesta.getBody().getArguments().getTorrentDuplicate();
                } else {
                    log.info("Torrent añadido: {}", respuesta.getBody());
                    transmissionTorrent = respuesta.getBody().getArguments().getTorrentAdded();
                }
                return transmissionTorrent;
            }
            log.warn("Error al añadir el torrent: {}", respuesta.getBody().getResult());
            torrent.setStatus(StatusTorrent.PENDING);
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId != null) {
                return addTorrent(transmissionServiceRequest);
            }
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
        return null;
    }

    private void getResponseHeaders(TransmissionServerRequest server, HttpHeaders headers) {
        try {
            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(new TransmissionRequest(), headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(server.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);
            sessionId = respuesta.getHeaders().getFirst("X-Transmission-Session-Id");
        } catch (HttpClientErrorException.Conflict e) {
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
        } catch (ResourceAccessException e) {
            log.error("Transmission no está disponible.", e);
        }
    }

    private HttpHeaders createHeaders(TransmissionServerRequest transmissionServerRequest) {
        String auth = transmissionServerRequest.getUsername() + ":" + transmissionServerRequest.getPassword();
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.US_ASCII));
        String authHeader = "Basic " + new String(encodedAuth);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", authHeader);
        return headers;
    }
}