package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.CameraHealthUseCase;
import com.davidperezmillan.recopilador.domain.models.Camaras;
import com.davidperezmillan.recopilador.infrastructure.health.HealthCheckService;
import com.davidperezmillan.recopilador.infrastructure.health.HealthStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CameraHealthService implements CameraHealthUseCase {
    
    private final HealthCheckService healthCheckService;
    
    public CameraHealthService(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
    }
    

    /**
     * Verifica la salud de una cámara específica
     * @param cameraName nombre de la cámara a verificar
     * @return HealthStatus con el estado de la cámara
     * @throws IllegalArgumentException si la cámara no existe
     */
    @Override
    public HealthStatus checkCameraHealth(String cameraName) throws IllegalArgumentException {
        // Buscar la cámara por nombre
        for (Camaras camara : Camaras.values()) {
            if (camara.getNombre().equalsIgnoreCase(cameraName)) {
                HealthStatus statusWeb = healthCheckService.checkWebPageHealth(camara.getUrl());
                HealthStatus statusFile = healthCheckService.checkFileSystemHealth(camara.getDirectorio());
                return mergeHealthStatuses(statusWeb, statusFile);
            }
        }
        
        // Si no se encuentra por nombre, intentar por enum value
        try {
            Camaras camara = Camaras.valueOf(cameraName.toUpperCase());
            HealthStatus statusWeb = healthCheckService.checkWebPageHealth(camara.getUrl());
            HealthStatus statusFile = healthCheckService.checkFileSystemHealth(camara.getDirectorio());
            return mergeHealthStatuses(statusWeb, statusFile);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cámara no encontrada: " + cameraName);
        }
    }

    /**
     * Verifica la salud de todas las cámaras configuradas de forma asíncrona
     * @return Mono<HashMap> con el nombre de cada cámara y su estado de salud
     */
    @Override
    public HashMap<Camaras, HealthStatus> checkAllCamerasHealthAsync() {
        HashMap<Camaras, HealthStatus> healthChecks = new HashMap<>();

        for (Camaras camara : Camaras.values()) {
            HealthStatus statusWeb = healthCheckService.checkWebPageHealth(camara.getUrl());
            HealthStatus statusFile = healthCheckService.checkFileSystemHealth(camara.getDirectorio());
            HealthStatus status = mergeHealthStatuses(statusWeb, statusFile);
            healthChecks.put(camara, status);
        }
        return healthChecks;

    }

    private HealthStatus mergeHealthStatuses(HealthStatus statusWeb, HealthStatus statusFile) {
        HealthStatus response = HealthStatus.builder()
                .detailsWebsite(statusWeb.getDetailsWebsite())
                .eventsFile(statusFile.getEventsFile())
                .build();
        response.setHealthy(statusWeb.isHealthy() && statusFile.isHealthy());
        return response;
    }
}
