package com.davidperezmillan.recopilador.infrastructure.transmission.models.request;

import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.Arguments;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransmissionRequest {

    // "{\"method\": \"torrent-add\", \"arguments\": {\"filename\": \"%s\"}}", torrent.getMagnet());
    private String method;
    @JsonProperty("arguments")
    private ArgumentsRequest arguments;

}

