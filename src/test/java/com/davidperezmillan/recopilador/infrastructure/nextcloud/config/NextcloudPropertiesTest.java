package com.davidperezmillan.recopilador.infrastructure.nextcloud.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NextcloudPropertiesTest {

    @Test
    void shouldEncodeSpecialCharactersInRelativePath() {
        NextcloudProperties properties = new NextcloudProperties();
        properties.setUrl("http://192.168.68.195:8087");
        properties.setDavPath("/remote.php/dav/files/");
        properties.setUsername("user_bot");

        String url = properties.buildUrl("pequebot/videos/A very happy wife (1).mp4");

        assertEquals(
                "http://192.168.68.195:8087/remote.php/dav/files/user_bot/pequebot/videos/A%20very%20happy%20wife%20(1).mp4",
                url
        );
    }

    @Test
    void shouldPreserveTrailingSlashForDirectories() {
        NextcloudProperties properties = new NextcloudProperties();
        properties.setUrl("http://localhost:8080/");
        properties.setDavPath("remote.php/dav/files");
        properties.setUsername("usuário");

        String url = properties.buildUrl("Vídeos favoritos/");

        assertEquals(
                "http://localhost:8080/remote.php/dav/files/usu%C3%A1rio/V%C3%ADdeos%20favoritos/",
                url
        );
    }
}


