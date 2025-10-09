package com.davidperezmillan.recopilador.infrastructure.health.services;

import com.davidperezmillan.recopilador.infrastructure.health.models.HealthStatus;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HealthCheckServiceTest {
    private final HealthHostService service = new HealthHostService();

    @Test
    void testCheckWebPageHealth_ok() {
        HealthStatus status = service.checkWebPageHealth("https://www.google.com");
        assertEquals(status.getHealthy(), com.davidperezmillan.recopilador.infrastructure.health.models.StatusHealthyEnum.HEALTHY);
        assertTrue(status.getDetailsWebsite().contains("HTTP status: 200"));
    }

    @Test
    void testCheckWebPageHealth_fail() {
        HealthStatus status = service.checkWebPageHealth("http://localhost:9999/noexiste");
        assertNotEquals(status.getHealthy(), com.davidperezmillan.recopilador.infrastructure.health.models.StatusHealthyEnum.HEALTHY);
        assertTrue(status.getDetailsWebsite().toLowerCase().contains("error"));
    }


}
