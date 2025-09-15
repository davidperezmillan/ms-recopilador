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

    @JsonProperty("speed-limit-down-enabled")
    private boolean speedLimitDownEnabled;

    @JsonProperty("speed-limit-up-enabled")
    private boolean speedLimitUpEnabled;

    @JsonProperty("alt-speed-enabled")
    private boolean altSpeedEnabled;

    // si el campo no esta o es nulo, no se pinta en el json
    private int[] ids;

    @JsonProperty("download-dir") // Add this annotation
    private String downloadDir; // Add this field

    @JsonProperty("delete-local-data")
    private boolean deleteLocalData;

}
