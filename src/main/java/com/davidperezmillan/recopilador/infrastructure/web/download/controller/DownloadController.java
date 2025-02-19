package com.davidperezmillan.recopilador.infrastructure.web.download.controller;

import com.davidperezmillan.recopilador.apllication.usecases.DownloadUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import com.davidperezmillan.recopilador.infrastructure.web.download.dtos.DownloadRequest;
import com.davidperezmillan.recopilador.infrastructure.web.download.mappers.DownloadRequestMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/download")
public class DownloadController {

    private final DownloadUseCase downloadUseCase;

    public DownloadController(DownloadUseCase downloadUseCase) {
        this.downloadUseCase = downloadUseCase;
    }

    @PostMapping("/torrent")
    public ResponseEntity<ApplicationResponse<String>> addTransmission(@RequestBody DownloadRequest downloadRequest) {
        boolean add = downloadUseCase.addDownload(DownloadRequestMapping.map(downloadRequest));
        if (!add) {
            return ResponseEntity.ok(new ApplicationResponse<String>(0, "Transmission not added"));
        }
        return ResponseEntity.ok(new ApplicationResponse<String>(1, "Transmission added"));
    }

    @PostMapping("/transmission")
    public ResponseEntity<ApplicationResponse<String>> downloadTransmission(){
        try {
            downloadUseCase.downloadAllTorrent("series");
        }catch (TransmissionException e){
            return ResponseEntity.status(409).body(new ApplicationResponse<String>(0, e.getMessage()));
        }
        return ResponseEntity.ok(new ApplicationResponse<String>(0, "Transmission lancher"));
    }
}
