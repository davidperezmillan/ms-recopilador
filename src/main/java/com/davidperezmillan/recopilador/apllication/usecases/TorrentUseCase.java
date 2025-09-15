package com.davidperezmillan.recopilador.apllication.usecases;

import com.davidperezmillan.recopilador.domain.models.Download;

import java.util.List;

public interface TorrentUseCase {


    boolean saveTorrent(Download download);

    void addTorrents();

    List<String> getDownloadDir(String server);

    List<String> getServers();

    void toggleAltSpeed(String server, boolean altSpeed);

    String getAltSpeed(String server);

    Integer[] deleteOldTorrents(String server, boolean deleteData, int days);

    Integer deleteTorrent(String server, boolean deleteData, int id);
}
