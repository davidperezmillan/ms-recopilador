package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.services;

import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.mappers.TorrentMapper;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.repositories.TorrentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TorrentService {

    private final TorrentRepository torrentRepository;

    public TorrentService(TorrentRepository torrentRepository) {
        this.torrentRepository = torrentRepository;
    }

    public boolean addTorrent(Download download) {
        Torrent respuesta = torrentRepository.save(TorrentMapper.map(download));
        if (respuesta != null) {
            return true;
        }
        return false;
    }

    public List<Torrent> getAllTorrents() {
        return torrentRepository.findAll();
    }
}
