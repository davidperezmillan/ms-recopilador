package com.davidperezmillan.recopilador.infrastructure.health;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventsFile {

    private String path;
    private boolean status;
    private String[] EventDir;
}
