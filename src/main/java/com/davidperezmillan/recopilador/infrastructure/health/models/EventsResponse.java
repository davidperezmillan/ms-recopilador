package com.davidperezmillan.recopilador.infrastructure.health.models;

import lombok.Data;

import java.util.List;

@Data
public class EventsResponse {
    private List<EventRecord> records;
}
