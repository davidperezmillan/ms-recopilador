package com.davidperezmillan.recopilador.infrastructure.transmission.exceptions;

import lombok.Getter;

public class TransmissionException extends Exception {

    @Getter
    private String code = "0000";
    @Getter
    private String message;

    public TransmissionException(String message) {
        super(message);
        this.message = message;
    }

    public TransmissionException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public TransmissionException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    }
    public TransmissionException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }
}
