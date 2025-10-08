package com.davidperezmillan.recopilador.infrastructure.health;

public class HealthCheckService {
    public HealthStatus checkWebPageHealth(String url) {
        try {
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.connect();
            int code = connection.getResponseCode();
            return new HealthStatus(code == 200, "HTTP status: " + code);
        } catch (Exception e) {
            return new HealthStatus(false, "Error: " + e.getMessage());
        }
    }

    public HealthStatus checkFileSystemHealth(String path) {
        java.io.File file = new java.io.File(path);
        boolean exists = file.exists();
        return new HealthStatus(exists, exists ? "Path exists" : "Path not found");
    }
}

