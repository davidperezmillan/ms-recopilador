package com.davidperezmillan.recopilador.infrastructure.web.transmission.options.controller;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transmission/options")
public class TransmissionOptionsController {

    private final TorrentUseCase torrentUseCase;

    public TransmissionOptionsController(TorrentUseCase torrentUseCase) {
        this.torrentUseCase = torrentUseCase;
    }


    /**
     * get alt speed
     * @param server
     * @return
     * @throws TransmissionException
     */
    @GetMapping("/altspeed/{server}")
    public ResponseEntity<ApplicationResponse<String>> getAltSpeed(
            @PathVariable("server") String server) throws TransmissionException {
        String altSpeedValue = torrentUseCase.getAltSpeed(server);
        return  ResponseEntity.ok(new ApplicationResponse<String>(1, altSpeedValue));
    }

    /**
     * toggle alt speed
     * @param server
     * @param altSpeed
     * @return
     * @throws TransmissionException
     */
    @PostMapping("/altspeed/{server}/{altSpeed}")
    public ResponseEntity<ApplicationResponse<String>> toogleAltSpeed(
            @PathVariable("server") String server,
            @PathVariable("altSpeed") boolean altSpeed) throws TransmissionException {
        torrentUseCase.toggleAltSpeed(server, altSpeed);
        return  ResponseEntity.ok(new ApplicationResponse<String>(1, "Alt Speed changed"));
    }
}
