package com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.repositories;

import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.Torrent;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransmissionRepository extends JpaRepository<Transmission, Long> {
}
