package com.davidperezmillan.recopilador.infrastructure.web.nextcloud.controller;

import com.davidperezmillan.recopilador.infrastructure.nextcloud.models.NextcloudFile;
import com.davidperezmillan.recopilador.infrastructure.nextcloud.services.NextcloudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NextcloudController.class)
class NextcloudControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NextcloudService nextcloudService;

    @Test
    void shouldReturnOnlyFilesFromFolder() throws Exception {
        List<NextcloudFile> resources = List.of(
                NextcloudFile.builder().name("movie.mkv").directory(false).build(),
                NextcloudFile.builder().name("Subcarpeta").directory(true).build(),
                NextcloudFile.builder().name("subtitle.srt").directory(false).build()
        );
        when(nextcloudService.listFiles("Peliculas/2026")).thenReturn(resources);
        mockMvc.perform(get("/nextcloud/files").param("path", "Peliculas/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2))
                .andExpect(jsonPath("$.data[0].name").value("movie.mkv"))
                .andExpect(jsonPath("$.data[1].name").value("subtitle.srt"));
    }

    @Test
    void shouldReturnBadRequestWhenPathIsEmpty() throws Exception {
        mockMvc.perform(get("/nextcloud/files").param("path", " "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.count").value(0));
    }

    @Test
    void shouldReturnRandomVideoMetadataFromFolder() throws Exception {
        List<NextcloudFile> resources = List.of(
                NextcloudFile.builder().name("folder").directory(true).build(),
                NextcloudFile.builder().name("notes.txt").directory(false).contentType("text/plain").build(),
                NextcloudFile.builder().name("movie.mkv").directory(false).build()
        );

        when(nextcloudService.listFiles("Peliculas/Random")).thenReturn(resources);

        mockMvc.perform(get("/nextcloud/files/random-video/info").param("path", "Peliculas/Random"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andExpect(jsonPath("$.data[0].name").value("movie.mkv"));
    }

    @Test
    void shouldStreamRandomVideoFromFolder() throws Exception {
        List<NextcloudFile> resources = List.of(
                NextcloudFile.builder().name("movie.mp4").directory(false).contentType("video/mp4").contentLength(4L).build()
        );

        when(nextcloudService.listFiles("Peliculas/Stream")).thenReturn(resources);
        when(nextcloudService.downloadFileAsStream("Peliculas/Stream/movie.mp4"))
                .thenReturn(new ByteArrayInputStream(new byte[]{1, 2, 3, 4}));

        mockMvc.perform(get("/nextcloud/files/random-video").param("path", "Peliculas/Stream"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "video/mp4"))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("inline")))
                .andExpect(header().string("Content-Disposition", org.hamcrest.Matchers.containsString("movie.mp4")))
                .andExpect(content().bytes(new byte[]{1, 2, 3, 4}));
    }

    @Test
    void shouldReturnNotFoundWhenFolderHasNoVideosForStream() throws Exception {
        List<NextcloudFile> resources = List.of(
                NextcloudFile.builder().name("folder").directory(true).build(),
                NextcloudFile.builder().name("notes.txt").directory(false).contentType("text/plain").build(),
                NextcloudFile.builder().name("image.jpg").directory(false).contentType("image/jpeg").build()
        );

        when(nextcloudService.listFiles("Peliculas/SinVideos")).thenReturn(resources);

        mockMvc.perform(get("/nextcloud/files/random-video").param("path", "Peliculas/SinVideos"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenPathIsEmptyForStream() throws Exception {
        mockMvc.perform(get("/nextcloud/files/random-video").param("path", " "))
                .andExpect(status().isBadRequest());
    }
}
