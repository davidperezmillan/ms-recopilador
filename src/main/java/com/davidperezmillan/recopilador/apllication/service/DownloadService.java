package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.DownloadUseCase;
import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.mappers.TorrentMapper;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.services.TorrentService;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.services.TransmissionService;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.transmission.services.TransmissionServerService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadService implements DownloadUseCase {

    private final TorrentService torrentService;
    private final TransmissionService transmissionService;
    private final TransmissionServerService transmissionServerService;

    public DownloadService(TorrentService torrentService,
                           TransmissionService transmissionService, TransmissionServerService transmissionServerService) {
        this.torrentService = torrentService;
        this.transmissionService = transmissionService;
        this.transmissionServerService = transmissionServerService;
    }

    public boolean addDownload(Download download) {
        torrentService.addTorrent(download);
        return true;
    }

    public void downloadAllTorrent() throws TransmissionException {
        // get all torrents
        List<Torrent> torrents = torrentService.getAllTorrents();
        // Download torrent
        // Add torrent to transmission
        try {
            transmissionServerService.setTransmission(transmissionService.findbyId(1L));
            for (Torrent torrent : torrents) {
                torrentService.save(transmissionServerService.addTorrent(torrent));
            }
        } catch (TransmissionException e) {
            throw e;
        }


    }
}
