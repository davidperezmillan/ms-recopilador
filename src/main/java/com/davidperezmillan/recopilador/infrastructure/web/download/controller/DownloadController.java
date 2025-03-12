package com.davidperezmillan.recopilador.infrastructure.web.download.controller;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import com.davidperezmillan.recopilador.infrastructure.web.download.dtos.DownloadRequest;
import com.davidperezmillan.recopilador.infrastructure.web.download.mappers.DownloadRequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/torrent")
public class DownloadController {

    private final TorrentUseCase torrentUseCase;

    public DownloadController(TorrentUseCase torrentUseCase) {
        this.torrentUseCase = torrentUseCase;
    }

    /**
     * add torrents to BBDD
     * @return
     */
    @PostMapping("/save")
    public ResponseEntity<ApplicationResponse<String>> saveTorrent(@RequestBody DownloadRequest downloadRequest) {
        boolean add = torrentUseCase.saveTorrent(DownloadRequestMapping.map(downloadRequest));
        if (!add) {
            return ResponseEntity.ok(new ApplicationResponse<String>(0, "torrent not added"));
        }
        return ResponseEntity.ok(new ApplicationResponse<String>(1, "Torrents added"));
    }

    /**
     * add torrents to transmission
     * @return
     */
    @PostMapping("/add")
    public ResponseEntity<ApplicationResponse<String>> addTorrents() {
        torrentUseCase.addTorrents();
        return ResponseEntity.ok(new ApplicationResponse<String>(1, "Transmission Torrents lauched"));
    }

    /**
     * Get all torrents form server
     * @return list of torrents
     */
    @GetMapping("/dir/{server}")
    public ResponseEntity<ApplicationResponse<List<String>>> getDownloadDir(@PathVariable("server") String server) throws TransmissionException {
        List<String> downloadDir = torrentUseCase.getDownloadDir(server);
        return ResponseEntity.ok(new ApplicationResponse<List<String>>(downloadDir.size(), downloadDir));
    }

}
