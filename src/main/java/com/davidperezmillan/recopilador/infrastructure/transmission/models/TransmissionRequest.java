package com.davidperezmillan.recopilador.infrastructure.transmission.models;

import lombok.Data;

@Data
public class TransmissionRequest {

    // "{\"method\": \"torrent-add\", \"arguments\": {\"filename\": \"%s\"}}", torrent.getMagnet());
    private String method;
    private Arguments arguments;

}

