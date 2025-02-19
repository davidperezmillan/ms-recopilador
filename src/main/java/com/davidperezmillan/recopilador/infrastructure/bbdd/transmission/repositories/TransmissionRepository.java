package com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.repositories;

import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TransmissionRepository extends JpaRepository<Transmission, Long> {

    Optional<Transmission> findByName(String name);
}
