package com.davidperezmillan.recopilador.infrastructure.health;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class HealthCheckServiceTest {
    private final HealthCheckService service = new HealthCheckService();

    @Test
    void testCheckWebPageHealth_ok() {
        HealthStatus status = service.checkWebPageHealth("https://www.google.com");
        assertTrue(status.isHealthy());
        assertTrue(status.getDetails().contains("HTTP status: 200"));
    }

    @Test
    void testCheckWebPageHealth_fail() {
        HealthStatus status = service.checkWebPageHealth("http://localhost:9999/noexiste");
        assertFalse(status.isHealthy());
        assertTrue(status.getDetails().toLowerCase().contains("error"));
    }

    @Test
    void testCheckFileSystemHealth_exists() throws Exception {
        File temp = File.createTempFile("test", ".tmp");
        HealthStatus status = service.checkFileSystemHealth(temp.getAbsolutePath());
        assertTrue(status.isHealthy());
        assertEquals("Path exists", status.getDetails());
        temp.delete();
    }

    @Test
    void testCheckFileSystemHealth_notExists() {
        HealthStatus status = service.checkFileSystemHealth("/no/existe/este/path");
        assertFalse(status.isHealthy());
        assertEquals("Path not found", status.getDetails());
    }
}

