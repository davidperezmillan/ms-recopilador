package com.davidperezmillan.recopilador.infrastructure.health.services;

import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;
import com.davidperezmillan.recopilador.infrastructure.health.models.EventsFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class HealthCheckService {

    @Value("${app.health.directory}")
    private String healthDirectory;

    public HealthStatus checkWebPageHealth(String url) {
        try {
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            connection.connect();
            int code = connection.getResponseCode();

            // Considerar tanto 200 como 401 como estados saludables
            boolean isHealthy = (code == 200 || code == 401);
            String details = "HTTP status: " + code;

            if (code == 401) {
                details += " (Camera is responding but requires authentication)";
            }

            return HealthStatus.builder().healthy(isHealthy).detailsWebsite(details).build();
        } catch (Exception e) {
            return HealthStatus.builder().healthy(false).detailsWebsite("Error: " + e.getMessage()).build();
        }
    }

    public HealthStatus checkFileSystemHealth(String camaraPath) {
        String path = healthDirectory + "/" + camaraPath;
        java.io.File file = new java.io.File(path);
        boolean exists = file.exists();

        if (!exists) {
            //devuelve el campo detailsFile como String[] vacio
            EventsFile emptyArray = EventsFile.builder()
                    .path(path)
                    .status(false)
                    .EventDir(new String[]{"No existe el directorio"})
                    .build();
            return HealthStatus.builder().healthy(false).eventsFile(emptyArray).build();
        }

        // Recorre el directorio y cuenta los subdirectorios
        try {
            java.io.File[] directories = file.listFiles(java.io.File::isDirectory);
            if (directories == null) {
                EventsFile emptyArray = EventsFile.builder()
                        .path(path)
                        .status(false)
                        .EventDir(new String[]{"No se pudo leer el directorio"})
                        .build();
                return HealthStatus.builder().healthy(false).eventsFile(emptyArray).build();
            }

            // Crear array con los nombres de los subdirectorios
            String[] directoryNames = new String[directories.length];
            for (int i = 0; i < directories.length; i++) {
                directoryNames[i] = directories[i].getName();
            }

            // Ordenar los nombres de los directorios según el patrón [año]Y[mes]M[dia]D[hora]H, más reciente primero
            java.util.Arrays.sort(directoryNames, (a, b) -> {
                // Extraer los valores numéricos del patrón
                java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("(\\d+)Y(\\d+)M(\\d+)D(\\d+)H");
                java.util.regex.Matcher ma = pattern.matcher(a);
                java.util.regex.Matcher mb = pattern.matcher(b);
                if (ma.matches() && mb.matches()) {
                    // Construir una fecha para cada directorio
                    java.time.LocalDateTime da = java.time.LocalDateTime.of(
                        Integer.parseInt(ma.group(1)), // año
                        Integer.parseInt(ma.group(2)), // mes
                        Integer.parseInt(ma.group(3)), // día
                        Integer.parseInt(ma.group(4)), // hora
                        0
                    );
                    java.time.LocalDateTime db = java.time.LocalDateTime.of(
                        Integer.parseInt(mb.group(1)),
                        Integer.parseInt(mb.group(2)),
                        Integer.parseInt(mb.group(3)),
                        Integer.parseInt(mb.group(4)),
                        0
                    );
                    // Orden descendente: más reciente primero
                    return db.compareTo(da);
                }
                // Si no coincide el patrón, dejar al final
                if (ma.matches()) return -1;
                if (mb.matches()) return 1;
                return a.compareTo(b);
            });

            // Crear el objeto EventsFile con todos los directorios encontrados
            EventsFile eventsFile = EventsFile.builder()
                    .path(path)
                    .status(true)
                    .EventDir(directoryNames)
                    .build();

            // Retornar el resultado exitoso
            return HealthStatus.builder()
                    .healthy(true)
                    .eventsFile(eventsFile)
                    .build();

        } catch (Exception e) {
            EventsFile emptyArray = EventsFile.builder()
                    .path(path)
                    .status(false)
                    .EventDir(new String[]{"No se pudo leer el directorio"})
                    .build();
            return HealthStatus.builder().healthy(false).eventsFile(emptyArray).build();
        }

    }
}
