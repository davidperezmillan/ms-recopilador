package com.davidperezmillan.recopilador.infrastructure.nextcloud.services;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.config.NextcloudProperties;
import com.github.sardine.Sardine;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
class NextcloudServiceTest {
    @Test
    void shouldUseEncodedUrlWhenDownloadingStream() throws Exception {
        Sardine sardine = mock(Sardine.class);
        NextcloudProperties properties = new NextcloudProperties();
        properties.setUrl("http://192.168.68.195:8087");
        properties.setDavPath("/remote.php/dav/files/");
        properties.setUsername("user_bot");
        NextcloudService service = new NextcloudService(sardine, properties);
        InputStream expectedStream = new ByteArrayInputStream(new byte[]{1, 2, 3});
        String expectedUrl = "http://192.168.68.195:8087/remote.php/dav/files/user_bot/pequebot/videos/A%20very%20happy%20wife%20(1).mp4";
        when(sardine.get(expectedUrl)).thenReturn(expectedStream);
        InputStream result = service.downloadFileAsStream("pequebot/videos/A very happy wife (1).mp4");
        assertSame(expectedStream, result);
        verify(sardine).get(expectedUrl);
    }
}
