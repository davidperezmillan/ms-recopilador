package com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request;

import lombok.Data;

@Data
public class ServerTransmission {

    private String url;
    private String username;
    private String password;
    
}
