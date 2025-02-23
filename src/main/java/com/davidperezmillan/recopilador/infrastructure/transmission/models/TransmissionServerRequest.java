package com.davidperezmillan.recopilador.infrastructure.transmission.models;

import lombok.Data;

@Data
public class TransmissionServerRequest {

    private String name;
    private String url;
    private String username;
    private String password;
}
