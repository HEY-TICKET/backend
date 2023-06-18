package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class JwtValidationException extends RuntimeException {

    private InternalCode code;

    public JwtValidationException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public JwtValidationException(String message) {
        super(message);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtValidationException(Throwable cause) {
        super(cause);
    }
}
