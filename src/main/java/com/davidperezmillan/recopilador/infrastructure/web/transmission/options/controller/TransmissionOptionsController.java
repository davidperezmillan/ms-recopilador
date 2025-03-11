package com.davidperezmillan.recopilador.infrastructure.web.transmission.options.controller;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/transmission/options")
public class TransmissionOptionsController {

    private final TorrentUseCase torrentUseCase;

    public TransmissionOptionsController(TorrentUseCase torrentUseCase) {
        this.torrentUseCase = torrentUseCase;
    }

    /**
     * toggle alt speed
     * @param server
     * @param altSpeed
     * @return
     * @throws TransmissionException
     */
    @GetMapping("/altspeed/{server}/{altSpeed}")
    public ResponseEntity<ApplicationResponse<String>> toogleAltSpeed(
            @PathVariable("server") String server,
            @PathVariable("altSpeed") boolean altSpeed) throws TransmissionException {
        torrentUseCase.toggleAltSpeed(server, altSpeed);
        return  ResponseEntity.ok(new ApplicationResponse<String>(1, "Alt Speed changed"));
    }
}
