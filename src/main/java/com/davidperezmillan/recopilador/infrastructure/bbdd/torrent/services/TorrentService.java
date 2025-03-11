package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.services;

import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.mappers.TorrentMapper;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.repositories.TorrentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.net.URLDecoder;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Service
@Log4j2
public class TorrentService {

    private final TorrentRepository torrentRepository;

    public TorrentService(TorrentRepository torrentRepository) {
        this.torrentRepository = torrentRepository;
    }


    public List<Torrent> getAllTorrents() {
        return torrentRepository.findAll();
    }

    public Torrent addTorrent(Download download) throws SQLIntegrityConstraintViolationException {
        return save(sanitizeTorrent(TorrentMapper.map(download)));
    }

    public Torrent save(Torrent torrent) {
        return torrentRepository.save(torrent);
    }


    private Torrent sanitizeTorrent(Torrent torrent) {
        try {
            String magnetLink = URLDecoder.decode(torrent.getUrl().trim(), "UTF-8");
            magnetLink = magnetLink.replace("magnet:///?xt=", "magnet:?xt=");
            magnetLink = magnetLink.replace(" ", "+");
            torrent.setUrl(magnetLink);
        } catch (Exception e) {
            log.info("Error decoding url: " + torrent.getUrl());
        }
        return torrent;
    }
}