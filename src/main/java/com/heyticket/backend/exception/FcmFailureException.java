package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class FcmFailureException extends RuntimeException {

    private InternalCode code;

    public FcmFailureException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public FcmFailureException(String message) {
        super(message);
    }

    public FcmFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public FcmFailureException(Throwable cause) {
        super(cause);
    }
}
