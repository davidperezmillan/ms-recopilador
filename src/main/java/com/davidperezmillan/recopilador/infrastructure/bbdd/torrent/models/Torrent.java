package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models;


import com.davidperezmillan.recopilador.infrastructure.bbdd.transmission.models.Transmission;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "`torrent`")
public class Torrent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "id_transmission")
    private int idTransmission;

    private String title;

    @Column(length = 2048, unique = true) // Increase the size limit
    private String url;

    @Column(name = "download_path")
    private String downloadPath;

    private StatusTorrent status;

    private String hashString;

    private double percentDone;


    @ManyToOne
    @JoinColumn(name = "server_id")
    private Transmission transmission;



}