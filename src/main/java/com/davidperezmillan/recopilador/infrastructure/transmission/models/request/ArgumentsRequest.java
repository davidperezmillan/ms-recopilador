package com.davidperezmillan.recopilador.infrastructure.transmission.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArgumentsRequest {

    //"{\"method\": \"torrent-add\", \"arguments\": {\"filename\": \"%s\"}}", torrent.getMagnet());
    private String filename;
    // "{\"method\": \"torrent-get\", \"arguments\": {\"fields\": [\"id\", \"name\", \"status\", \"percentDone\"]}}";
    private String[] fields;

    // si el campo no esta o es nulo, no se pinta en el json
    private String[] ids;

    @JsonProperty("download-dir") // Add this annotation
    private String downloadDir; // Add this field

}
