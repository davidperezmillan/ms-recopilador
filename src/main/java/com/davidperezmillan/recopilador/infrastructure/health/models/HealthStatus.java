package com.davidperezmillan.recopilador.infrastructure.health.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthStatus {
    private boolean healthy;
    private String details;
    private String detailsWebsite;
    private EventsFile eventsFile;

}
