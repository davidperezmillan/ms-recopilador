package com.davidperezmillan.recopilador.infrastructure.web.download.dtos;

import lombok.Data;

@Data
public class DownloadRequest {

    private String url;
    private String downloadPath;
    private String serverName;
}
