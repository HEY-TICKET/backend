package com.heyticket.backend.exception;

import lombok.Getter;

@Getter
public class ValidationFailureException extends RuntimeException {

    private InternalCode code;

    public ValidationFailureException(String message, InternalCode code) {
        super(message);
        this.code = code;
    }

    public ValidationFailureException(String message) {
        super(message);
    }

    public ValidationFailureException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationFailureException(Throwable cause) {
        super(cause);
    }
}
