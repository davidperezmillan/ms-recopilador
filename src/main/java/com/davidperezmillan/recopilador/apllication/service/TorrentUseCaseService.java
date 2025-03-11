package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.mappers.TorrentMapper;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.services.TorrentService;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.services.TransmissionService;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AddTransmissionRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.AllTorrentRequest;
import com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request.ServerTransmission;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import com.davidperezmillan.recopilador.infrastructure.transmission.services.TransmissionServerService;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

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
        torrentService.getAllTorrents().forEach(torrent -> {

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
    public void toggleAltSpeed(String server, boolean altSpeed) {
        Transmission transmission = transmissionService.findbyName(server);
        ServerTransmission serverTransmission = new ServerTransmission();
        serverTransmission.setUrl(transmission.getUrl());
        serverTransmission.setUsername(transmission.getUsername());
        serverTransmission.setPassword(transmission.getPassword());
        transmissionServerService.setGlobalSpeedLimits(serverTransmission, altSpeed);

    }
}
