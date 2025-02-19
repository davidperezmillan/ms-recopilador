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



    public List<Torrent> getAllTorrents() {
        return torrentRepository.findAll();
    }

    public Torrent addTorrent(Download download) {
        return save(TorrentMapper.map(download));
    }
    public Torrent save(Torrent torrent) {
        return torrentRepository.save(torrent);
    }
}
