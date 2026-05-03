package com.davidperezmillan.recopilador.infrastructure.web.nextcloud.controller;

import com.davidperezmillan.recopilador.infrastructure.nextcloud.exceptions.NextcloudException;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.models.NextcloudFile;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.services.NextcloudService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

@Tag(name = "Nextcloud", description = "Operaciones de ficheros y carpetas en Nextcloud via WebDAV")
@RestController
@RequestMapping("/nextcloud")
public class NextcloudController {

    private static final Set<String> VIDEO_EXTENSIONS = Set.of(
            "mp4", "mkv", "avi", "mov", "wmv", "flv", "webm", "mpeg", "mpg", "m4v"
    );

    private final NextcloudService nextcloudService;

    public NextcloudController(NextcloudService nextcloudService) {
        this.nextcloudService = nextcloudService;
    }

    @Operation(
            summary = "Listar ficheros de una carpeta",
            description = "Recupera todos los ficheros de una carpeta en Nextcloud (no incluye subcarpetas)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ficheros obtenidos correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"count\":2,\"data\":[{\"name\":\"pelicula.mkv\",\"directory\":false},{\"name\":\"subtitulo.srt\",\"directory\":false}]}"))),
            @ApiResponse(responseCode = "400", description = "Ruta de carpeta no valida"),
            @ApiResponse(responseCode = "502", description = "Error al consultar Nextcloud")
    })
    @GetMapping("/files")
    public ResponseEntity<NextcloudFilesResponse> getFilesByFolder(
            @Parameter(description = "Ruta relativa de la carpeta en Nextcloud", example = "Peliculas/2026")
            @RequestParam("path") String path) {

        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().body(new NextcloudFilesResponse(0, List.of()));
        }

        try {
            List<NextcloudFile> files = nextcloudService.listFiles(path).stream()
                    .filter(file -> !file.isDirectory())
                    .toList();

            return ResponseEntity.ok(new NextcloudFilesResponse(files.size(), files));
        } catch (NextcloudException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new NextcloudFilesResponse(0, List.of()));
        }
    }

    @Operation(
            summary = "Obtener un video aleatorio de una carpeta",
            description = "Recupera los metadatos de un archivo de video aleatorio dentro de la carpeta indicada en Nextcloud."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Video aleatorio obtenido correctamente",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"count\":1,\"data\":[{\"name\":\"pelicula.mkv\",\"directory\":false}]}"))),
            @ApiResponse(responseCode = "400", description = "Ruta de carpeta no valida"),
            @ApiResponse(responseCode = "404", description = "No hay videos en la carpeta"),
            @ApiResponse(responseCode = "502", description = "Error al consultar Nextcloud")
    })
    @GetMapping("/files/random-video/info")
    public ResponseEntity<NextcloudFilesResponse> getRandomVideoInfoByFolder(
            @Parameter(description = "Ruta relativa de la carpeta en Nextcloud", example = "Peliculas/2026")
            @RequestParam("path") String path) {

        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().body(new NextcloudFilesResponse(0, List.of()));
        }

        try {
            NextcloudFile randomVideo = getRandomVideo(path);

            if (randomVideo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new NextcloudFilesResponse(0, List.of()));
            }

            return ResponseEntity.ok(new NextcloudFilesResponse(1, List.of(randomVideo)));
        } catch (NextcloudException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new NextcloudFilesResponse(0, List.of()));
        }
    }

    @Operation(
            summary = "Reproducir un video aleatorio de una carpeta",
            description = "Devuelve el contenido binario de un video aleatorio de la carpeta indicada para ser consumido por un frontend web."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Stream del video obtenido correctamente",
                    content = @Content(mediaType = "video/*")),
            @ApiResponse(responseCode = "400", description = "Ruta de carpeta no valida"),
            @ApiResponse(responseCode = "404", description = "No hay videos en la carpeta"),
            @ApiResponse(responseCode = "502", description = "Error al consultar Nextcloud")
    })
    @GetMapping("/files/random-video")
    public ResponseEntity<InputStreamResource> streamRandomVideoByFolder(
            @Parameter(description = "Ruta relativa de la carpeta en Nextcloud", example = "Peliculas/2026")
            @RequestParam("path") String path) {

        if (path == null || path.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            NextcloudFile randomVideo = getRandomVideo(path);
            if (randomVideo == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String relativeFilePath = buildChildPath(path, randomVideo.getName());
            InputStreamResource resource = new InputStreamResource(nextcloudService.downloadFileAsStream(relativeFilePath));
            MediaType mediaType = resolveMediaType(randomVideo);

            ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline()
                                    .filename(randomVideo.getName(), StandardCharsets.UTF_8)
                                    .build()
                                    .toString());

            if (randomVideo.getContentLength() > 0) {
                responseBuilder.contentLength(randomVideo.getContentLength());
            }

            return responseBuilder.body(resource);
        } catch (NextcloudException ex) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }
    }

    private boolean isVideoFile(NextcloudFile file) {
        if (file.getContentType() != null && file.getContentType().toLowerCase(Locale.ROOT).startsWith("video/")) {
            return true;
        }

        String name = file.getName();
        if (name == null || !name.contains(".")) {
            return false;
        }

        String extension = name.substring(name.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
        return VIDEO_EXTENSIONS.contains(extension);
    }

    private NextcloudFile getRandomVideo(String path) {
        List<NextcloudFile> videos = nextcloudService.listFiles(path).stream()
                .filter(file -> !file.isDirectory())
                .filter(this::isVideoFile)
                .toList();

        if (videos.isEmpty()) {
            return null;
        }

        return videos.get(ThreadLocalRandom.current().nextInt(videos.size()));
    }

    private String buildChildPath(String parentPath, String fileName) {
        String normalizedParentPath = parentPath.endsWith("/") ? parentPath.substring(0, parentPath.length() - 1) : parentPath;
        return normalizedParentPath + "/" + fileName;
    }

    private MediaType resolveMediaType(NextcloudFile file) {
        if (file.getContentType() != null && !file.getContentType().isBlank()) {
            return MediaType.parseMediaType(file.getContentType());
        }

        return MediaTypeFactory.getMediaType(file.getName()).orElse(MediaType.APPLICATION_OCTET_STREAM);
    }

    public record NextcloudFilesResponse(int count, List<NextcloudFile> data) {
    }
}





