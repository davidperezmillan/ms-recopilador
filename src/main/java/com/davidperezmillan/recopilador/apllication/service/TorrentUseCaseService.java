package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.domain.models.NameTorrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.mappers.TorrentMapper;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.StatusTorrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.services.TorrentService;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.services.TransmissionService;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AddTransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AllTorrentRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.ServerTransmission;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionResponse;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import com.davidperezmillan.recopilador.infrastructure.transmission.services.TransmissionServerService;
import lombok.extern.log4j.Log4j2;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Log4j2
public class TorrentUseCaseService implements TorrentUseCase {

    private final TransmissionService transmissionService;
    private final TorrentService torrentService;

    private final TransmissionServerService transmissionServerService;

    public TorrentUseCaseService(TransmissionService transmissionService,
                                 TorrentService torrentService,
                                 TransmissionServerService transmissionServerService) {
        this.transmissionService = transmissionService;
        this.torrentService = torrentService;
        this.transmissionServerService = transmissionServerService;
    }

    @Override
    public boolean saveTorrent(Download download) {
        // recuperar el transmission server
        Transmission transmission = transmissionService.findbyName(download.getServerName());

        Torrent torrent = TorrentMapper.map(download);
        torrent.setTransmission(transmission);

        // grabar el torrent
        try {
            torrentService.save(torrent);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.error("Torrent duplicado");
        } catch (Exception e) {
            log.error("Error GENERAL al guardar el torrent : {}", e.getMessage());

        }
        return false;
    }

    @Override
    public void addTorrents() {
        // recuperar todos los torrent

        // añadir al log un campo con un literal

        MDC.put("type-log", "FUNCIONAL");
        log.info("Recuperando torrents pendientes de descarga");

        torrentService.getTorrentsByStatus(StatusTorrent.PENDING_DOWNLOAD).forEach(torrent -> {

            log.info("Añadiendo torrent: {}", torrent.getUrl());
            // creamos el request
            AddTransmissionRequest addTransmissionRequest = new AddTransmissionRequest();
            addTransmissionRequest.setMangetLink(torrent.getUrl());
            addTransmissionRequest.setDownloadPath(torrent.getDownloadPath());
            ServerTransmission serverTransmission = new ServerTransmission();
            serverTransmission.setUrl(torrent.getTransmission().getUrl());
            serverTransmission.setUsername(torrent.getTransmission().getUsername());
            serverTransmission.setPassword(torrent.getTransmission().getPassword());
            addTransmissionRequest.setServer(serverTransmission);

            // añadir el torrent
            TransmissionTorrent transmissionTorrent = transmissionServerService.addTorrent(addTransmissionRequest);
            log.info("Torrent añadido: {}", transmissionTorrent);

            if (transmissionTorrent != null) {
                // actualizar el torrent
                torrent.setIdTransmission(transmissionTorrent.getId());
                torrent.setTitle(transmissionTorrent.getName());
                torrent.setHashString(transmissionTorrent.getHashString());
                torrent.setPercentDone(transmissionTorrent.getPercentDone());
                torrent.setStatus(StatusTorrent.DOWNLOADING);
                torrentService.save(torrent);
            }

        });

    }

    @Override
    public List<String> getDownloadDir(String server) {
        Transmission transmission = transmissionService.findbyName(server);
        AllTorrentRequest allTorrentRequest = new AllTorrentRequest();
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        allTorrentRequest.setServer(serverTransmission);

        return transmissionServerService.getListDownloadDir(allTorrentRequest);
    }

    @Override
    public List<String> getServers(){
        List<Transmission> servers = transmissionService.findAll();
        // Recuperar los nombres de los servidores
        return servers.stream().map(Transmission::getName).toList();
    }

    @Override
    public void toggleAltSpeed(String server, boolean altSpeed) {
        Transmission transmission = transmissionService.findbyName(server);
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        transmissionServerService.setAltSpeedEnabled(serverTransmission, altSpeed);

    }

    @Override
    public String getAltSpeed(String server) {
        Transmission transmission = transmissionService.findbyName(server);
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        return transmissionServerService.getAltSpeedEnabled(serverTransmission);
    }

    @Override
    public Integer[] deleteOldTorrents(String server, boolean deleteData, int days) {
        Transmission transmission = transmissionService.findbyName(server);
        AllTorrentRequest allTorrentRequest = new AllTorrentRequest();
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        allTorrentRequest.setServer(serverTransmission);
        List<TransmissionTorrent> oldTorrent = transmissionServerService.getOldTorrent(allTorrentRequest, deleteData, days);
        // delete old torrents transmissionServerService
        Integer[] idsBorrados = oldTorrent.stream()
                .filter(torrent -> transmissionServerService.deleteTorrent(serverTransmission, torrent.getId(), deleteData) != null)
                .map(TransmissionTorrent::getId)
                .toArray(Integer[]::new);
        return idsBorrados;
    }

    @Override
    public Integer deleteTorrent(String server, boolean deleteData, int id) {
        Transmission transmission = transmissionService.findbyName(server);
        AllTorrentRequest allTorrentRequest = new AllTorrentRequest();
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        allTorrentRequest.setServer(serverTransmission);
        TransmissionResponse resp = transmissionServerService.deleteTorrent(serverTransmission, id, deleteData);
        log.info("Respuesta al borrar el torrent: {}", resp);
        return id;
    }

    @Override
    public List<NameTorrent> findDownloadDirByName(String server, String name) {
        Transmission transmission = transmissionService.findbyName(server);
        AllTorrentRequest allTorrentRequest = new AllTorrentRequest();
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        allTorrentRequest.setServer(serverTransmission);

        List<String> downloadsDir = transmissionServerService.getListDownloadDir(allTorrentRequest);
        log.info("Filtering download dirs by name: {}", name);
        name = name.trim();
        // quitar las comillas dobles si las tiene
        name = name.replaceAll("^\"|\"$", "");
        // if name is a magnet link, get the name from the magnet link
        if (name.startsWith("magnet:")) {
            log.info("Name is a magnet link, extracting torrent name from magnet link");
            String torrentName = getTorrentNameFromMagnet(name);
            if (torrentName != null && !torrentName.isEmpty()) {
                log.info("Extracted torrent name from magnet link: {}", torrentName);
                name = torrentName;
            }
        }
        log.info("Filtered name to search: {}", name);

        // recuperar las 2 primeras palabras si existen o una sola palabra
        String[] words = name.split("\\s+");
        String filteredName;
        if (words.length >= 2) {
            filteredName = words[0] + " " + words[1];
        } else if (words.length == 1) {
            filteredName = words[0];
        } else {
            filteredName = name; // Si no hay palabras, usar el nombre completo
        }
        String finalName = name;

        String propousedDir = "/downloads/complete/"+finalName+"/Session 1";
        log.info("Propoused dir: {}", propousedDir);

        List<NameTorrent> response = downloadsDir.stream()
                .filter(dir -> dir.toLowerCase().contains(filteredName.toLowerCase()))
                .map(dir -> new NameTorrent(finalName, dir, propousedDir))
                .toList();

        if (response.isEmpty()) {
            log.info("No download dirs found matching the name: {}", filteredName);
            NameTorrent notFound = new NameTorrent(finalName, null, propousedDir);
            response = List.of(notFound);
        }
        return response;
    }

    private static String getTorrentNameFromMagnet(String magnetLink) {
        log.info("Extracting torrent name from magnet link: {}", magnetLink);
        try {
            // Dividir los parámetros del magnet link
            String[] parts = magnetLink.split("\\?");
            if (parts.length < 2) {
                return null; // No hay parámetros
            }

            String[] params = parts[1].split("&");
            Map<String, String> paramMap = new HashMap<>();

            // Parsear los parámetros
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    paramMap.put(keyValue[0], URLDecoder.decode(keyValue[1], "UTF-8"));
                }
            }

            // Retornar el valor del parámetro "dn" (Display Name)
            String dn =  paramMap.get("dn");
            // eliminar los textos dentro de corchetes []
            dn = dn.replaceAll("\\[.*?\\]", "").trim();
            return dn;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
