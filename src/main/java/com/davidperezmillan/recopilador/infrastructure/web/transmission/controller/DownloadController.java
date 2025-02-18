package com.davidperezmillan.recopilador.infrastructure.web.transmission.controller;

import com.davidperezmillan.recopilador.apllication.usecases.DownloadUseCase;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import com.davidperezmillan.recopilador.infrastructure.web.transmission.dtos.DownloadRequest;
import com.davidperezmillan.recopilador.infrastructure.web.transmission.mappers.DownloadRequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
