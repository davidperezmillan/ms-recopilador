package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.repositories;

import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.StatusTorrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TorrentRepository extends JpaRepository<Torrent, Long> {

    List<Torrent> findByStatus(StatusTorrent status);

    List<Torrent> findByStatusOrStatusIsNull(StatusTorrent status);
}
