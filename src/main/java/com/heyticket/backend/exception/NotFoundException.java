package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {

    private InternalCode code;

    public NotFoundException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }
}
