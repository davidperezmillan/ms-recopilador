package com.davidperezmillan.recopilador.infrastructure.health;

public class HealthStatus {
    private boolean healthy;
    private String details;

    public HealthStatus(boolean healthy, String details) {
        this.healthy = healthy;
        this.details = details;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public String getDetails() {
        return details;
    }
}

