package com.davidperezmillan.recopilador.infrastructure.web.download.controller;

import com.davidperezmillan.recopilador.apllication.usecases.TorrentUseCase;
import com.davidperezmillan.recopilador.domain.models.NameTorrent;
import com.davidperezmillan.recopilador.infrastructure.transmission.exceptions.TransmissionException;
import com.davidperezmillan.recopilador.infrastructure.transmission.models.response.TransmissionTorrent;
import com.davidperezmillan.recopilador.infrastructure.web.ApplicationResponse;
import com.davidperezmillan.recopilador.infrastructure.web.download.dtos.DownloadRequest;
import com.davidperezmillan.recopilador.infrastructure.web.download.mappers.DownloadRequestMapping;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@Tag(name = "Descargas", description = "Operaciones sobre torrents y descargas")
@RestController
@RequestMapping("/torrent")
public class DownloadController {

    private final TorrentUseCase torrentUseCase;

    public DownloadController(TorrentUseCase torrentUseCase) {
        this.torrentUseCase = torrentUseCase;
    }

    /**
     * Guarda torrents en la base de datos.
     */
    @Operation(summary = "Guardar torrent", description = "Agrega torrents a la base de datos.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torrent guardado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":\"Torrents added\"}")))
    })
    @PostMapping("/save")
    public ResponseEntity<ApplicationResponse<String>> saveTorrent(@RequestBody DownloadRequest downloadRequest) {
        boolean add = torrentUseCase.saveTorrent(DownloadRequestMapping.map(downloadRequest));
        if (!add) {
            return ResponseEntity.ok(new ApplicationResponse<String>(0, "torrent not added"));
        }
        return ResponseEntity.ok(new ApplicationResponse<String>(1, "Torrents added"));
    }

    /**
     * Agrega torrents a Transmission.
     */
    @Operation(summary = "Agregar torrents a Transmission", description = "Agrega los torrents almacenados a Transmission.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torrents lanzados",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":\"Transmission Torrents lauched\"}")))
    })
    @PostMapping("/add")
    public ResponseEntity<ApplicationResponse<String>> addTorrents() {
        torrentUseCase.addTorrents();
        return ResponseEntity.ok(new ApplicationResponse<String>(1, "Transmission Torrents lauched"));
    }

    /**
     * Get all download dir from server
     * @return list of torrents
     */
    @Operation(summary = "Obtener directorio de descargas", description = "Obtiene todos los directorios de descarga del servidor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de directorios",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":2,\"data\":[\"/downloads/dir1\",\"/downloads/dir2\"]}")))
    })
    @GetMapping("/dir/{server}")
    public ResponseEntity<ApplicationResponse<List<String>>> getDownloadDir(
        @Parameter(description = "Nombre del servidor", example = "server1")
        @PathVariable("server") String server) throws TransmissionException {
        List<String> downloadDir = torrentUseCase.getDownloadDir(server);
        return ResponseEntity.ok(new ApplicationResponse<List<String>>(downloadDir.size(), downloadDir));
    }

    /**
     * find download dir from server by name
     * @param server
     * @param name
     * @return list of paths download dir
     * @throws TransmissionException
     */
    @Operation(summary = "Buscar directorio por nombre", description = "Busca directorios de descarga por nombre en el servidor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista de coincidencias",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":[{\"name\":\"torrent1\",\"path\":\"/downloads/torrent1\"}]}")))
    })
    @PostMapping("/dir/{server}")
    public ResponseEntity<ApplicationResponse<List<NameTorrent>>> findDownloadDirByName(
        @Parameter(description = "Nombre del servidor", example = "server1")
        @PathVariable("server") String server,
        @Parameter(description = "Nombre a buscar", example = "torrent1")
        @RequestBody String name) throws TransmissionException {
        List<NameTorrent> downloadDir = torrentUseCase.findDownloadDirByName(server, name);
        return ResponseEntity.ok(new ApplicationResponse<List<NameTorrent>>(downloadDir.size(), downloadDir));
    }


    @Operation(summary = "Eliminar torrents antiguos", description = "Elimina torrents antiguos del servidor.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torrents eliminados",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":2,\"data\":[\"1\",\"2\"]}")))
    })
    @DeleteMapping("/delete/old/{server}")
    public ResponseEntity<ApplicationResponse<String[]>> deleteOldTorrents(
        @Parameter(description = "Nombre del servidor", example = "server1")
        @PathVariable("server") String server,
        @Parameter(description = "Días de antigüedad", example = "30")
        @RequestParam("days")  int days,
        @Parameter(description = "Eliminar datos asociados", example = "true")
        @RequestParam("deleteData") boolean deleteData) throws TransmissionException {
        Integer[] respuesta = torrentUseCase.deleteOldTorrents(server, deleteData, days);
        return ResponseEntity.ok(new ApplicationResponse<String[]>(respuesta.length, Arrays.stream(respuesta).map(String::valueOf).toArray(String[]::new)));
    }

    @Operation(summary = "Eliminar torrent por ID", description = "Elimina un torrent específico por su ID.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Torrent eliminado",
            content = @Content(mediaType = "application/json",
                examples = @ExampleObject(value = "{\"code\":1,\"data\":[\"3\"]}")))
    })
    @DeleteMapping("/delete/{server}/{id}")
    public ResponseEntity<ApplicationResponse<String[]>> deleteTorrentById(
        @Parameter(description = "Nombre del servidor", example = "server1")
        @PathVariable("server") String server,
        @Parameter(description = "ID del torrent", example = "3")
        @PathVariable("id") String id,
        @Parameter(description = "Eliminar datos asociados", example = "true")
        @RequestParam("deleteData") boolean deleteData) throws TransmissionException {
        int respuesta = torrentUseCase.deleteTorrent(server, deleteData, Integer.parseInt(id));
        String stringIds = String.valueOf(respuesta);
        return ResponseEntity.ok(new ApplicationResponse<String[]>(1, new String[]{stringIds}));
    }
}
