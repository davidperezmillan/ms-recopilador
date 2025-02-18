package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models;


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

    @Column(length = 1024) // Increase the size limit
    private String url;

    private StatusTorrent status;

    private String hashString;

    private double percentDone;



}