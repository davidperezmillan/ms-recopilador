package com.davidperezmillan.recopilador.infrastructure.health;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HealthStatus {
    private boolean healthy;
    @JsonProperty("details_website")
    private String detailsWebsite;

    @JsonProperty("details_file")
    private String[] eventsFile;
}

