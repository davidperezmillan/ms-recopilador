package com.davidperezmillan.recopilador.infrastructure.transmission.dtos.request;

import lombok.Data;

@Data
public class AddTransmissionRequest {

    private String mangetLink;
    private String downloadPath;
    private ServerTransmission server;
}
