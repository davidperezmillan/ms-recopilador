package com.davidperezmillan.recopilador.infrastructure.health;


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
            String[] emptyArray = new String[0];
            return HealthStatus.builder().healthy(false).eventsFile(emptyArray).build();
        }
        // Recorre el directorio y cuenta los subdirectorios
        String[] eventsFile = file.list((current, name) -> new java.io.File(current, name).isDirectory());
        return HealthStatus.builder().healthy(true).eventsFile(eventsFile).build();
    }
}
