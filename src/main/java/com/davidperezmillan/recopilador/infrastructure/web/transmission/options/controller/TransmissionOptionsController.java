package com.davidperezmillan.recopilador.infrastructure.web.transmission.options.controller;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Opciones de Transmission", description = "Operaciones sobre opciones y servidores de Transmission")
@RestController
@RequestMapping("/transmission/options")
public class TransmissionOptionsController {

    private final TorrentUseCase torrentUseCase;

    public TransmissionOptionsController(TorrentUseCase torrentUseCase) {
        this.torrentUseCase = torrentUseCase;
    }


    /**
     * Obtiene la lista de servidores de Transmission disponibles.
     */
    @Operation(summary = "Listar servidores", description = "Obtiene la lista de servidores de Transmission disponibles.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de servidores obtenida",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":[\"server1\",\"server2\"]}")))
    })
    @GetMapping("/servers")
    public ResponseEntity<ApplicationResponse<List<String>>> getServers() {
        List<String> servers = torrentUseCase.getServers();
        return ResponseEntity.ok(new ApplicationResponse<>(servers.size(), servers));
    }

    /**
     * Obtiene el valor de alt speed para un servidor.
     */
    @Operation(summary = "Obtener alt speed", description = "Obtiene el valor de alt speed para un servidor de Transmission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Valor alt speed obtenido",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":\"true\"}")))
    })
    @GetMapping("/altspeed/{server}")
    public ResponseEntity<ApplicationResponse<String>> getAltSpeed(
            @Parameter(description = "Nombre del servidor", example = "server1")
            @PathVariable("server") String server) {
        String altSpeedValue = torrentUseCase.getAltSpeed(server);
        return ResponseEntity.ok(new ApplicationResponse<>(1, altSpeedValue));
    }

    /**
     * Alterna el valor de alt speed para un servidor.
     */
    @Operation(summary = "Alternar alt speed", description = "Alterna el valor de alt speed para un servidor de Transmission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Alt speed cambiado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":\"Alt Speed changed\"}")))
    })
    @PostMapping("/altspeed/{server}/{altSpeed}")
    public ResponseEntity<ApplicationResponse<String>> toogleAltSpeed(
            @Parameter(description = "Nombre del servidor", example = "server1")
            @PathVariable("server") String server,
            @Parameter(description = "Nuevo valor de alt speed (true/false)", example = "true")
            @PathVariable("altSpeed") String altSpeed) {
        boolean altSpeedBool = Boolean.parseBoolean(altSpeed);
        torrentUseCase.toggleAltSpeed(server, altSpeedBool);
        return ResponseEntity.ok(new ApplicationResponse<>(1, "Alt Speed changed"));
    }
}
