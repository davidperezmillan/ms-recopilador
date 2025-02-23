package com.davidperezmillan.recopilador.infrastructure.transmission.models;

import com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models.StatusTorrent;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class TransmissionServiceRespone {


    private int idTransmission;

    private String title;

    private String url;

    private String downloadPath;

    private StatusTorrent status;

    private String hashString;

    private double percentDone;

}
