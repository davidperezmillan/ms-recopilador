package com.davidperezmillan.recopilador.infrastructure.web.camara.dtos;

import lombok.Data;

import java.util.List;

@Data
public class EventsResponse {
    private List<EventRecord> records;
}
