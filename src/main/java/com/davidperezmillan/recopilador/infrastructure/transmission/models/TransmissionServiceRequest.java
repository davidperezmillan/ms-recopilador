package com.davidperezmillan.recopilador.infrastructure.transmission.models;

import lombok.Data;

@Data
public class TransmissionServiceRequest {

    private String url;
    private String downloadPath;
    private String hashString;

    private TransmissionServerRequest transmissionServerRequest;
}
