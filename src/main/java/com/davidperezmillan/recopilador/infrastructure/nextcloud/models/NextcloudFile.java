package com.davidperezmillan.recopilador.infrastructure.nextcloud.models;
import lombok.Builder;
import lombok.Data;
import java.time.Instant;
@Data
@Builder
public class NextcloudFile {
    /** Nombre del archivo o carpeta */
    private String name;
    /** Ruta completa en Nextcloud */
    private String path;
    /** Indica si es un directorio */
    private boolean directory;
    /** Tamaño en bytes (0 para directorios) */
    private long contentLength;
    /** Tipo MIME del contenido */
    private String contentType;
    /** Fecha de última modificación */
    private Instant lastModified;
    /** ETag del recurso */
    private String etag;
}
