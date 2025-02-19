package com.davidperezmillan.recopilador.infrastructure.transmission.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Arguments {

    //"{\"method\": \"torrent-add\", \"arguments\": {\"filename\": \"%s\"}}", torrent.getMagnet());
    private String filename;
    // "{\"method\": \"torrent-get\", \"arguments\": {\"fields\": [\"id\", \"name\", \"status\", \"percentDone\"]}}";
    private String[] fields;

    //{"torrent-added":{"hashString":"549219ff8e36a84966f80aa3209c78856d8ad5df","id":9,"name":"New.Sakura.Hell.Loantown.25.02.06.BigTits.Brunette.Hardcore.DemonINC.https.bigwarp.io.embed.vlbb613atjlu.html.mp4"}},"result":"success"}
    @JsonProperty("torrent-added")
    private TransmissionTorrent torrentAdded;

    @JsonProperty("torrent-duplicate")
    private TransmissionTorrent torrentDuplicate;

    @JsonProperty("torrents")
    private TransmissionTorrent[] torrents;
}
