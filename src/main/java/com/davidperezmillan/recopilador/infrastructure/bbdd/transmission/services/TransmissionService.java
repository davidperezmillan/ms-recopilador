package com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.services;

import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.repositories.TransmissionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class TransmissionService {

    private final TransmissionRepository transmissionRepository;

    public TransmissionService(TransmissionRepository transmissionRepository) {
        this.transmissionRepository = transmissionRepository;
    }

    public Transmission findbyId(Long id) {
        return transmissionRepository.findById(id).orElse(null);
    }

    public List<Transmission> findAll() {
        return transmissionRepository.findAll();
    }
}
