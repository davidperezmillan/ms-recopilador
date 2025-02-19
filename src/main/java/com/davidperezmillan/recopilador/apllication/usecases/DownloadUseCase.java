package com.davidperezmillan.recopilador.apllication.usecases;

import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;

public interface DownloadUseCase {

    boolean addDownload(Download download);

    void downloadAllTorrent(String nameServer) throws TransmissionException;
}
