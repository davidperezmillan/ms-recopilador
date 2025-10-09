package com.davidperezmillan.recopilador.infrastructure.health.services;

import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealthCheckServiceTest {
    private final HealthCheckService service = new HealthCheckService();

    @Test
    void testCheckWebPageHealth_ok() {
        HealthStatus status = service.checkWebPageHealth("https://www.google.com");
        assertTrue(status.isHealthy());
        assertTrue(status.getDetailsWebsite().contains("HTTP status: 200"));
    }

    @Test
    void testCheckWebPageHealth_fail() {
        HealthStatus status = service.checkWebPageHealth("http://localhost:9999/noexiste");
        assertFalse(status.isHealthy());
        assertTrue(status.getDetailsWebsite().toLowerCase().contains("error"));
    }


}
