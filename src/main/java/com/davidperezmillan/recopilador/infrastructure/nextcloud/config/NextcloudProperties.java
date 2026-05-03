package com.davidperezmillan.recopilador.infrastructure.nextcloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.springframework.web.util.UriUtils;

@Data
@Component
@ConfigurationProperties(prefix = "app.nextcloud")
public class NextcloudProperties {

    /** URL base del servidor Nextcloud, ej: https://nextcloud.example.com */
    private String url;

    /** Usuario de Nextcloud */
    private String username;

    /**
     * Token de aplicación de Nextcloud.
     * Genéralo en: Configuración de usuario → Seguridad → Tokens de aplicación.
     * Se envía como contraseña en Basic Auth (estándar WebDAV de Nextcloud).
     */
    private String token;

    /**
     * Ruta base WebDAV, por defecto para Nextcloud:
     * /remote.php/dav/files/{username}/
     */
    private String davPath = "/remote.php/dav/files/";

    /**
     * Devuelve la URL base de WebDAV para el usuario configurado.
     */
    public String getWebDavBaseUrl() {
        String normalizedUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
        String normalizedDavPath = davPath.startsWith("/") ? davPath : "/" + davPath;
        normalizedDavPath = normalizedDavPath.endsWith("/") ? normalizedDavPath : normalizedDavPath + "/";
        return normalizedUrl + normalizedDavPath + UriUtils.encodePathSegment(username, StandardCharsets.UTF_8) + "/";
    }

    /**
     * Devuelve la URL WebDAV completa para una ruta relativa dada.
     * @param relativePath ruta relativa dentro del home del usuario
     */
    public String buildUrl(String relativePath) {
        String base = getWebDavBaseUrl();
        if (relativePath == null || relativePath.isBlank()) {
            return base;
        }
        String normalizedPath = relativePath.startsWith("/") ? relativePath.substring(1) : relativePath;
        boolean hasTrailingSlash = normalizedPath.endsWith("/");

        String encodedPath = Arrays.stream(normalizedPath.split("/"))
                .filter(segment -> !segment.isBlank())
                .map(segment -> UriUtils.encodePathSegment(segment, StandardCharsets.UTF_8))
                .reduce((left, right) -> left + "/" + right)
                .orElse("");

        if (hasTrailingSlash && !encodedPath.isEmpty()) {
            encodedPath = encodedPath + "/";
        }

        return base + encodedPath;
    }
}



