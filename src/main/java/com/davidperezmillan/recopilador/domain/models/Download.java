package com.davidperezmillan.recopilador.domain.models;

import lombok.Data;

@Data
public class Download {

    private String url;
    private String downloadPath;
    private String serverName;
}
