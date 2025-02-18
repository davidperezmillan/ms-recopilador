package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.repositories;

import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TorrentRepository extends JpaRepository<Torrent, Long> {
}
