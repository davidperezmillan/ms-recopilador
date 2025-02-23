package com.davidperezmillan.recopilador.infrastructure.transmission;

import com.davidperezmillan.recopilador.infrastructure.transmission.models.TransmissionServiceRequest;

public class TransmissionServiceResponseMapper {

    public static TransmissionServiceResponse mapToTransmissionServiceResponse(TransmissionServiceRequest transmissionServiceRequest) {
        TransmissionServiceResponse transmissionServiceResponse = new TransmissionServiceResponse();
        transmissionServiceResponse.setDownloadPath(transmissionServiceRequest.getDownloadPath());
        transmissionServiceResponse.setUrl(transmissionServiceRequest.getUrl());
        transmissionServiceResponse.setHashString(transmissionServiceRequest.getHashString());
        transmissionServiceResponse.setTransmissionServerResponse(TransmissionServerResponse.builder()
                .name(transmissionServiceRequest.getTransmissionServerRequest().getName())
                .url(transmissionServiceRequest.getTransmissionServerRequest().getUrl())
                .username(transmissionServiceRequest.getTransmissionServerRequest().getUsername())
                .password(transmissionServiceRequest.getTransmissionServerRequest().getPassword())
                .build());
        return transmissionServiceResponse;
    }
}
