package com.davidperezmillan.recopilador.infrastructure.transmission.services;


import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.StatusTorrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.Arguments;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionResponse;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionTorrent;
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
public class TransmissionServerService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Setter
    private Transmission transmission;

    private String sessionId;


    public List<TransmissionTorrent> listTorrent() throws TransmissionException {
        try {
            if (sessionId == null) {
                HttpHeaders headers = createHeaders();
                getResponseHeaders(headers);
            }
        } catch (TransmissionException e) {
            //log.warn("Transmission no disponible");
            throw e;
        }
        try {
            log.info("Session ID: {}", sessionId);
            // Crear la solicitud para añadir el enlace magnet
            HttpHeaders headers = createHeaders();

            headers.set("X-Transmission-Session-Id", sessionId);

            headers.set("Content-Type", "application/json");

            //String jsonPayload = "{\"method\": \"torrent-get\", \"arguments\": {\"fields\": [\"id\", \"name\", \"status\", \"percentDone\"]}}";
            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-get");
            Arguments arguments = new Arguments();
            arguments.setFields(new String[]{"id", "name", "status", "percentDone", "hashString"});
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);

        /*
        ResponseEntity<String> respuesta = restTemplate.exchange(transmissionUrl, HttpMethod.POST, requestEntity, String.class);
        log.info("Respuesta: {}", respuesta.getBody());
        */

            // Enviar la solicitud POST para añadir el torrent

            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmission.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);

            if (null != respuesta.getBody().getArguments().getTorrents()) {
                return List.of(respuesta.getBody().getArguments().getTorrents());
            }


        } catch (HttpClientErrorException.Conflict e) {
            // Capturar el error 409 y obtener el nuevo ID de sesión
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId == null) {
                throw new TransmissionException("9999", "No se pudo obtener el ID de sesión de Transmission.");
            }
        } catch (ResourceAccessException e) {  // capturamos si transmission no esta disponible
            throw new TransmissionException("9999", "Transmission no está disponible.");
        }
        return List.of();
    }


    public Torrent addTorrent(Torrent torrent) throws TransmissionException {
        try {
            if (sessionId == null) {
                HttpHeaders headers = createHeaders();
                getResponseHeaders(headers);
            }
        } catch (TransmissionException e) {
            // log.warn("Transmission no disponible");
            throw e;
        }
        try {
            // Crear la solicitud para añadir el enlace magnet
            HttpHeaders headers = createHeaders();

            headers.set("X-Transmission-Session-Id", sessionId);

            headers.set("Content-Type", "application/json");

            TransmissionRequest request = new TransmissionRequest();
            request.setMethod("torrent-add");

            Arguments arguments = new Arguments();
            arguments.setFilename(torrent.getUrl());
            request.setArguments(arguments);

            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(request, headers);

            // Enviar la solicitud POST para añadir el torrent
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


                // prepare torrrent to save
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
            // Capturar el error 409 y obtener el nuevo ID de sesión
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId == null) {
                throw new TransmissionException("0001", "No se pudo obtener el ID de sesión de Transmission.");
            }
        } catch (ResourceAccessException e) {  // capturamos si transmission no esta disponible
            throw new TransmissionException("9999", "Transmission no está disponible.");
        }
        return null;
    }

    private void getResponseHeaders(HttpHeaders headers) throws TransmissionException {
        try {
            HttpEntity<TransmissionRequest> requestEntity = new HttpEntity<>(new TransmissionRequest(), headers);
            ResponseEntity<TransmissionResponse> respuesta = restTemplate.exchange(transmission.getUrl(), HttpMethod.POST, requestEntity, TransmissionResponse.class);
            sessionId = respuesta.getHeaders().getFirst("X-Transmission-Session-Id");
        } catch (HttpClientErrorException.Conflict e) {
            // Capturar el error 409 y obtener el nuevo ID de sesión
            sessionId = e.getResponseHeaders().getFirst("X-Transmission-Session-Id");
            log.info("Session ID by Error: {}", sessionId);
            if (sessionId == null) {
                throw new TransmissionException("0001", "No se pudo obtener el ID de sesión de Transmission.");
            }
        } catch (ResourceAccessException e) {  // capturamos si transmission no esta disponible
            throw new TransmissionException("9999", "Transmission no está disponible.");
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
