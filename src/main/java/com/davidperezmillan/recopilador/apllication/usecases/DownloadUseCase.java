package com.davidperezmillan.recopilador.apllication.usecases;

import com.davidperezmillan.recopilador.domain.models.Download;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;

import java.util.List;

public interface DownloadUseCase {

    boolean addDownload(Download download);

    void downloadAllTorrent(String nameServer) throws TransmissionException;

    List<TransmissionTorrent> getAllTransmission();

    TransmissionTorrent getTransmission(String hashString);
}
