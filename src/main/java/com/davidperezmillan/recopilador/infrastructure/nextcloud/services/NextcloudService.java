package com.davidperezmillan.recopilador.infrastructure.nextcloud.services;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.config.NextcloudProperties;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.exceptions.NextcloudException;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.models.NextcloudFile;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * Servicio para interactuar con Nextcloud via WebDAV.
 * Operaciones disponibles:
 *   - Listar archivos y carpetas
 *   - Subir archivos
 *   - Descargar archivos
 *   - Crear carpetas
 *   - Eliminar archivos/carpetas
 *   - Mover/renombrar archivos
 *   - Copiar archivos
 *   - Verificar existencia de un recurso
 */
@Service
@Log4j2
@RequiredArgsConstructor
public class NextcloudService {
    private final Sardine sardine;
    private final NextcloudProperties props;
    // -----------------------------------------------------------------------
    // Listar
    // -----------------------------------------------------------------------
    /**
     * Lista los recursos contenidos en la ruta indicada.
     *
     * @param relativePath ruta relativa dentro del home del usuario (ej: "Documentos/")
     * @return lista de {@link NextcloudFile} sin incluir el propio directorio raíz
     */
    public List<NextcloudFile> listFiles(String relativePath) {
        String url = props.buildUrl(relativePath);
        log.info("Listando recursos en: {}", url);
        try {
            List<DavResource> resources = sardine.list(url);
            return resources.stream()
                    .skip(1) // el primer elemento es el propio directorio
                    .map(this::toNextcloudFile)
                    .toList();
        } catch (IOException e) {
            log.error("Error al listar recursos en {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo listar el directorio: " + relativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Subir
    // -----------------------------------------------------------------------
    /**
     * Sube un archivo al servidor Nextcloud.
     *
     * @param relativePath ruta relativa de destino (incluye nombre de archivo, ej: "Documentos/foto.jpg")
     * @param data         contenido del archivo
     * @param contentType  tipo MIME (ej: "image/jpeg")
     */
    public void uploadFile(String relativePath, byte[] data, String contentType) {
        String url = props.buildUrl(relativePath);
        log.info("Subiendo archivo a: {}", url);
        try {
            sardine.put(url, data, contentType);
            log.info("Archivo subido correctamente: {}", url);
        } catch (IOException e) {
            log.error("Error al subir archivo a {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo subir el archivo: " + relativePath, e);
        }
    }
    /**
     * Sube un archivo al servidor Nextcloud usando un {@link InputStream}.
     * Útil para archivos grandes donde no conviene cargar todo en memoria.
     *
     * @param relativePath ruta relativa de destino
     * @param inputStream  stream con el contenido del archivo
     * @param contentType  tipo MIME
     */
    public void uploadFile(String relativePath, InputStream inputStream, String contentType) {
        String url = props.buildUrl(relativePath);
        log.info("Subiendo archivo (stream) a: {}", url);
        try {
            sardine.put(url, inputStream, contentType);
            log.info("Archivo subido correctamente: {}", url);
        } catch (IOException e) {
            log.error("Error al subir archivo (stream) a {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo subir el archivo: " + relativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Descargar
    // -----------------------------------------------------------------------
    /**
     * Descarga el contenido de un archivo como array de bytes.
     *
     * @param relativePath ruta relativa del archivo
     * @return contenido del archivo
     */
    public byte[] downloadFile(String relativePath) {
        String url = props.buildUrl(relativePath);
        log.info("Descargando archivo: {}", url);
        try (InputStream is = sardine.get(url)) {
            byte[] content = is.readAllBytes();
            log.info("Archivo descargado correctamente: {} ({} bytes)", url, content.length);
            return content;
        } catch (IOException e) {
            log.error("Error al descargar archivo {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo descargar el archivo: " + relativePath, e);
        }
    }
    /**
     * Devuelve un {@link InputStream} para leer el archivo de forma lazy.
     * El caller es responsable de cerrar el stream.
     *
     * @param relativePath ruta relativa del archivo
     * @return stream del contenido
     */
    public InputStream downloadFileAsStream(String relativePath) {
        String url = props.buildUrl(relativePath);
        log.info("Obteniendo stream de: {}", url);
        try {
            return sardine.get(url);
        } catch (IOException e) {
            log.error("Error al obtener stream de {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo obtener el stream del archivo: " + relativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Crear carpeta
    // -----------------------------------------------------------------------
    /**
     * Crea un directorio en Nextcloud.
     * Si ya existe, no lanza error.
     *
     * @param relativePath ruta relativa del directorio a crear
     */
    public void createFolder(String relativePath) {
        String url = props.buildUrl(relativePath);
        log.info("Creando carpeta: {}", url);
        try {
            if (!sardine.exists(url)) {
                sardine.createDirectory(url);
                log.info("Carpeta creada: {}", url);
            } else {
                log.info("La carpeta ya existe: {}", url);
            }
        } catch (IOException e) {
            log.error("Error al crear carpeta {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo crear la carpeta: " + relativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Eliminar
    // -----------------------------------------------------------------------
    /**
     * Elimina un archivo o carpeta (con todo su contenido) de Nextcloud.
     *
     * @param relativePath ruta relativa del recurso a eliminar
     */
    public void delete(String relativePath) {
        String url = props.buildUrl(relativePath);
        log.info("Eliminando recurso: {}", url);
        try {
            sardine.delete(url);
            log.info("Recurso eliminado: {}", url);
        } catch (IOException e) {
            log.error("Error al eliminar {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo eliminar el recurso: " + relativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Mover / Renombrar
    // -----------------------------------------------------------------------
    /**
     * Mueve o renombra un recurso en Nextcloud.
     *
     * @param sourceRelativePath ruta relativa de origen
     * @param destRelativePath   ruta relativa de destino
     * @param overwrite          sobreescribir si ya existe el destino
     */
    public void move(String sourceRelativePath, String destRelativePath, boolean overwrite) {
        String sourceUrl = props.buildUrl(sourceRelativePath);
        String destUrl = props.buildUrl(destRelativePath);
        log.info("Moviendo {} -> {}", sourceUrl, destUrl);
        try {
            sardine.move(sourceUrl, destUrl, overwrite);
            log.info("Recurso movido correctamente");
        } catch (IOException e) {
            log.error("Error al mover {} -> {}: {}", sourceUrl, destUrl, e.getMessage());
            throw new NextcloudException(
                    "No se pudo mover el recurso de " + sourceRelativePath + " a " + destRelativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Copiar
    // -----------------------------------------------------------------------
    /**
     * Copia un recurso en Nextcloud.
     *
     * @param sourceRelativePath ruta relativa de origen
     * @param destRelativePath   ruta relativa de destino
     * @param overwrite          sobreescribir si ya existe el destino
     */
    public void copy(String sourceRelativePath, String destRelativePath, boolean overwrite) {
        String sourceUrl = props.buildUrl(sourceRelativePath);
        String destUrl = props.buildUrl(destRelativePath);
        log.info("Copiando {} -> {}", sourceUrl, destUrl);
        try {
            sardine.copy(sourceUrl, destUrl, overwrite);
            log.info("Recurso copiado correctamente");
        } catch (IOException e) {
            log.error("Error al copiar {} -> {}: {}", sourceUrl, destUrl, e.getMessage());
            throw new NextcloudException(
                    "No se pudo copiar el recurso de " + sourceRelativePath + " a " + destRelativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Verificar existencia
    // -----------------------------------------------------------------------
    /**
     * Comprueba si un recurso existe en Nextcloud.
     *
     * @param relativePath ruta relativa del recurso
     * @return {@code true} si existe, {@code false} en caso contrario
     */
    public boolean exists(String relativePath) {
        String url = props.buildUrl(relativePath);
        try {
            return sardine.exists(url);
        } catch (IOException e) {
            log.error("Error al verificar existencia de {}: {}", url, e.getMessage());
            throw new NextcloudException("No se pudo verificar la existencia de: " + relativePath, e);
        }
    }
    // -----------------------------------------------------------------------
    // Métodos privados de utilidad
    // -----------------------------------------------------------------------
    private NextcloudFile toNextcloudFile(DavResource resource) {
        return NextcloudFile.builder()
                .name(resource.getName())
                .path(resource.getPath())
                .directory(resource.isDirectory())
                .contentLength(resource.getContentLength() != null ? resource.getContentLength() : 0L)
                .contentType(resource.getContentType())
                .lastModified(resource.getModified() != null ? resource.getModified().toInstant() : null)
                .etag(resource.getEtag())
                .build();
    }
}
