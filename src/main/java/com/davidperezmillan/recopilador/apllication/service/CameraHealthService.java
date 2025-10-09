package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.CameraHealthUseCase;
import com.davidperezmillan.recopilador.domain.models.Camaras;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventRecord;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventsResponse;
import com.davidperezmillan.recopilador.infrastructure.health.models.StatusHealthyEnum;
import com.davidperezmillan.recopilador.infrastructure.health.services.HealthHostService;
import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;
import com.davidperezmillan.recopilador.infrastructure.health.services.HealthWebService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class CameraHealthService implements CameraHealthUseCase {
    
    private final HealthHostService healthHostService;
    private final HealthWebService healthWebService;
    
    public CameraHealthService(HealthHostService healthHostService, HealthWebService healthWebService) {
        this.healthHostService = healthHostService;
        this.healthWebService = healthWebService;
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
                HealthStatus statusWeb = healthHostService.checkWebPageHealth(camara.getUrl());
                HealthStatus statusFile = healthHostService.checkFileSystemHealth(camara.getDirectorio());
                return mergeHealthStatuses(statusWeb, statusFile);
            }
        }
        
        // Si no se encuentra por nombre, intentar por enum value
        try {
            Camaras camara = Camaras.valueOf(cameraName.toUpperCase());
            HealthStatus statusWeb = healthHostService.checkWebPageHealth(camara.getUrl());
            HealthStatus statusFile = healthHostService.checkFileSystemHealth(camara.getDirectorio());
            return mergeHealthStatuses(statusWeb, statusFile);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cámara no encontrada: " + cameraName);
        }
    }

    @Override
    public EventsResponse checkCamaraEvents(String camara) {
        try{
            return healthWebService.getEventsDir(camara);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Cámara no encontrada: " + camara);
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
            HealthStatus statusWeb = healthHostService.checkWebPageHealth(camara.getUrl());
            HealthStatus statusFile = healthHostService.checkFileSystemHealth(camara.getDirectorio());
            HealthStatus status = mergeHealthStatuses(statusWeb, statusFile);
            HealthStatus statusEnriquecido = compareEvents(checkCamaraEvents(camara.getNombre()), status);
            healthChecks.put(camara, statusEnriquecido);

        }


        return healthChecks;

    }

    private HealthStatus mergeHealthStatuses(HealthStatus statusWeb, HealthStatus statusFile) {
        HealthStatus response = HealthStatus.builder()
                .detailsWebsite(statusWeb.getDetailsWebsite())
                .eventsFile(statusFile.getEventsFile())
                .build();
        if (statusWeb.getHealthy() == null || statusFile.getHealthy() == null) {
            response.setHealthy(null);
        } else if (statusWeb.getHealthy() == StatusHealthyEnum.HEALTHY && statusFile.getHealthy() == StatusHealthyEnum.HEALTHY) {
            response.setHealthy(StatusHealthyEnum.HEALTHY);
        } else {
            response.setHealthy(StatusHealthyEnum.UNHEALTHY);
        }
        return response;
    }


    private HealthStatus compareEvents(EventsResponse eventsResponse, HealthStatus healthStatus) {
        if (eventsResponse.getRecords() == null || eventsResponse.getRecords().isEmpty()) {
            healthStatus.setHealthy(StatusHealthyEnum.DEGRADED);
            return healthStatus;
        }
        List<EventRecord> eventFile = eventsResponse.getRecords();
        String[] eventHost = healthStatus.getEventsFile().getEventDir();

        // Comparar los eventos del archivo con los eventos del host
        for (EventRecord event : eventFile) {
            boolean encontrado = false;
            for (String hostEvent : eventHost) {
                if (event.getDirname().equalsIgnoreCase(hostEvent)) {
                    encontrado = true;
                    break;
                }
            }
            if (!encontrado) {
                healthStatus.setHealthy(StatusHealthyEnum.DEGRADED);
                return healthStatus;
            }
        }
        return healthStatus;
    }
}
