package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.DownloadUseCase;
import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.services.TorrentService;
import org.springframework.stereotype.Service;

@Service
public class DownloadService implements DownloadUseCase {

    private final TorrentService torrentService;

    public DownloadService(TorrentService torrentService) {
        this.torrentService = torrentService;
    }

    public boolean addDownload(Download download) {
        return torrentService.addTorrent(download);
    }
}
