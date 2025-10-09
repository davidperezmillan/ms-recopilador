package com.davidperezmillan.recopilador.apllication.service;

import com.davidperezmillan.recopilador.apllication.usecases.CameraHealthUseCase;
import com.davidperezmillan.recopilador.domain.models.Camaras;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventRecord;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventsResponse;
import com.davidperezmillan.recopilador.infrastructure.health.models.StatusHealthyEnum;
import com.davidperezmillan.recopilador.infrastructure.health.services.HealthHostService;
import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;
import com.davidperezmillan.recopilador.infrastructure.health.services.HealthWebService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@Log4j2
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

        firstEventFile = eventFile.get(0).getDirname();
        firstEventHost = eventHost.length > 0 ? eventHost[0] : null;
        log.info("First event from file system: " + firstEventFile);
        log.info("First event from host: " + firstEventHost);
        if (firstEventFile != null && firstEventHost != null && firstEventHost.contains(firstEventFile)) {
            log.info("First events match: " + firstEventHost + " contains " + firstEventFile);
            firstEventsMatch = true;
        } else {
            log.warn("First events do not match: " + firstEventHost + " does not contain " + firstEventFile);
            firstEventsMatch = false;
        }

        log.info("Comparing events from host and file system:");
        log.info("Events from host: " + eventHost);
        log.info("Events from file system: " + eventFile);


        /*
        boolean allEventsMatch = eventFile.stream()
                .allMatch(event -> {
                    String eventName = event.getDirname();
                    for (String dirName : eventHost) {
                        if (dirName.contains(eventName)) {
                            return true;
                        }
                    }
                    return false;
                });

         */

        healthStatus.setHealthy(allEventsMatch ? StatusHealthyEnum.HEALTHY : StatusHealthyEnum.DEGRADED);
        log.info("Final health status after comparing events: " + healthStatus.getHealthy());
        return healthStatus;
    }
}
