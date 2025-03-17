package com.davidperezmillan.recopilador.infrastructure.bbdd.torrent.models;

public enum StatusTorrent {

    PENDING_DOWNLOAD,
    DOWNLOADING,
    COMPLETE,
    ERROR,
    DELETED,
    UNKOWN;
}