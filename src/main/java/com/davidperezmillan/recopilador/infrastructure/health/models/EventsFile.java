package com.davidperezmillan.recopilador.infrastructure.health.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventsFile {

    private String path;
    private boolean status;
    private String error;
    private String[] eventDir;
}
