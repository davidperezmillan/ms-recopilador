package com.davidperezmillan.recopilador.apllication.usecases;

import com.davidperezmillan.recopilador.domain.models.Camaras;
import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;

import java.util.HashMap;

public interface CameraHealthUseCase {

    /**
     * Verifica la salud de todas las cámaras configuradas de forma asíncrona
     * @return Mono<HashMap> con el nombre de cada cámara y su estado de salud
     */
    HashMap<Camaras, HealthStatus> checkAllCamerasHealthAsync();

    /**
     * Verifica la salud de una cámara específica
     * @param cameraName nombre de la cámara a verificar
     * @return HealthStatus con el estado de la cámara
     * @throws IllegalArgumentException si la cámara no existe
     */
    HealthStatus checkCameraHealth(String cameraName) throws IllegalArgumentException;
}
