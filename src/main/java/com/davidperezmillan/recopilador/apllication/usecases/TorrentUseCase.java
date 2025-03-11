package com.davidperezmillan.recopilador.apllication.usecases;

import com.davidperezmillan.recopilador.domain.models.Download;

import java.util.List;

public interface TorrentUseCase {


    boolean saveTorrent(Download download);

    void addTorrents();

    List<String> getDownloadDir(String server);

    void toggleAltSpeed(String server, boolean altSpeed);
}
